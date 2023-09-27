package com.factset.sdk.eventdriven.factsettrading;

import com.factset.sdk.eventdriven.client.*;
import com.factset.sdk.eventdriven.factsettrading.model.OrderSubscriptionRequest;
import com.factset.sdk.eventdriven.factsettrading.model.OrderUpdateEvent;
import com.factset.sdk.eventdriven.model.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class OrderUpdateApi {
    private final WebsocketApiClient client;
    private static final Logger logger = LoggerFactory.getLogger("OrderUpdateApi");

    public OrderUpdateApi(WebsocketApiClient client) {
        this.client = client;
    }

//    public CompletableFuture<Subscription> subscribeOrderUpdates(BiConsumer<OrderUpdateEvent, Throwable> callback) {
//        List<String> subscribeList = Arrays.asList("orderupdates");
//        OrderSubscriptionRequest request = new OrderSubscriptionRequest(subscribeList);
//        return client.subscribe(request, OrderUpdateEvent.class, callback);
//    }

    public OrderUpdateSubscription subscribeOrderUpdates(OrderSubscriptionRequest request) {
        return new OrderUpdateSubscription(request);
    }

    public class OrderUpdateSubscription {

        private final OrderSubscriptionRequest request;

        private Consumer<OrderUpdateEvent> onOrderUpdateEvent;
        private Consumer<Meta> onMeta;
        private Consumer<Throwable> onError = t -> logger.warn("Exception in subscription", t);

        public OrderUpdateSubscription(OrderSubscriptionRequest request) {
            this.request = request;
        }

        public OrderUpdateSubscription onOrderUpdateEvent(Consumer<OrderUpdateEvent> onOrderUpdateEvent) {
            this.onOrderUpdateEvent = onOrderUpdateEvent;
            return this;
        }

        public OrderUpdateSubscription onMeta(Consumer<Meta> onMeta) {
            this.onMeta = onMeta;
            return this;
        }

        public OrderUpdateSubscription onError(Consumer<Throwable> onError) {
            this.onError = onError;
            return this;
        }

        public CompletableFuture<Subscription> subscribe() {
            return client.subscribe(request, (msg, t) -> {
                if (t != null) {
                    onError.accept(t);
                    return;
                }

                if (handleMessage(msg, OrderUpdateEvent.class, onOrderUpdateEvent)) return;
                if (handleMessage(msg, Meta.class, onMeta)) return;

                onError.accept(new UnexpectedMessageException("Unexpected Message", msg));
            });
        }

        private <T> boolean handleMessage(Message msg, Class<T> messageClass, Consumer<T> handler) {
            if (messageClass.getSimpleName().equals(msg.getType())) {
                if (handler != null) {
                    try {
                        handler.accept(msg.parseAs(messageClass));
                    } catch (MalformedMessageException ex) {
                        onError.accept(ex);
                    }
                } else {
                    logger.warn("Missing handler: on{}", messageClass.getSimpleName());
                }
                return true;
            }
            return false;
        }
    }
}

