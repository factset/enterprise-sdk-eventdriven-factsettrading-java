package com.factset.sdk.eventdriven.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;


class ExponentialBackoffRetry {

    private static final Logger logger = LoggerFactory.getLogger(ExponentialBackoffRetry.class);

    private static final Random random = new Random();

    /**
     * @param completableFutureSupplier produce an attempt as a {@link CompletableFuture}
     * @param maxAttempts               the number of maxAttempts to make before allowing failure
     * @param <T>                       the type of value the future will return
     * @return a composite {@link CompletableFuture} that runs until success or total failure
     */
    public static <T> CompletableFuture<T> withRetries(Supplier<CompletableFuture<T>> completableFutureSupplier,
                                                       int maxAttempts,
                                                       ScheduledExecutorService scheduledExecutorService
    ) {
        CompletableFuture<T> completableFuture = completableFutureSupplier.get();

        return flatten(completableFuture.thenApply(CompletableFuture::completedFuture)
                .exceptionally(throwable -> retry(completableFutureSupplier, 1, throwable, maxAttempts, scheduledExecutorService))
        );
    }

    private static <T> CompletableFuture<T> retry(Supplier<CompletableFuture<T>> completableFutureSupplier,
                                                  int currentAttempt,
                                                  Throwable throwable,
                                                  int maxAttempts,
                                                  ScheduledExecutorService scheduledExecutorService
    ) {
        // stop when reaching maxAttempts
        int nextAttempt = currentAttempt + 1;
        if (nextAttempt > maxAttempts) {
            return failedFuture(throwable);
        }

        return flatten(flatten(CompletableFuture.supplyAsync(completableFutureSupplier, getScheduler(currentAttempt, scheduledExecutorService)))
                .thenApply(CompletableFuture::completedFuture)
                .exceptionally(nextThrowable -> retry(completableFutureSupplier, nextAttempt, nextThrowable, maxAttempts, scheduledExecutorService)));
    }


    private static Executor getScheduler(int currentAttempt, ScheduledExecutorService scheduledExecutorService) {
        long exponentialWaitTime = (long) Math.pow(2, currentAttempt) * 1000 + random.nextInt(200);
        Executor scheduler = runnable -> scheduledExecutorService.schedule(runnable, exponentialWaitTime, TimeUnit.MILLISECONDS);
        logger.debug("attemptsSoFar: {} exponentialWaitTime: {}", currentAttempt, exponentialWaitTime);

        return scheduler;
    }

    private static <T> CompletableFuture<T> flatten(CompletableFuture<CompletableFuture<T>> completableCompletable) {
        return completableCompletable.thenCompose(Function.identity());
    }

    private static <T> CompletableFuture<T> failedFuture(Throwable t) {
        final CompletableFuture<T> cf = new CompletableFuture<>();
        cf.completeExceptionally(t);
        return cf;
    }
}
