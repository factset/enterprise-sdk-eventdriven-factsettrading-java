package com.factset.sdk.streaming.client;

import lombok.Value;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enables the decoupling between the client and models, without
 * the need for an interface in between.
 */
class ExtractedMeta {

    @Value
    private static class Accessors {
        Method setId;
        Method setTimeout;
        Method getTimeout;
    }

    private static final Map<Class<?>, Method> getMetaMethods = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Accessors> metaAccessors = new ConcurrentHashMap<>();

    private final Object meta;
    private final Accessors accessors;

    private ExtractedMeta(Object meta, Accessors a) {
        this.meta = meta;
        this.accessors = a;
    }

    public static ExtractedMeta extractMeta(Object o) {
        Object meta = getMetaOrThrow(o);
        Accessors a = getMetaAccessorsOrThrow(meta);

        return new ExtractedMeta(meta, a);
    }

    private static Object getMetaOrThrow(Object o) {
        Method getMeta = getMetaMethods.computeIfAbsent(o.getClass(), c -> findMethod(c, "getMeta"));

        try {
            return Objects.requireNonNull(getMeta.invoke(o), "meta is null");
        } catch (IllegalAccessException | NullPointerException e) {
            throw new InvalidRequestException(e);
        } catch (InvocationTargetException e) {
            throw new InvalidRequestException(e.getTargetException());
        }
    }

    private static Accessors getMetaAccessorsOrThrow(Object meta) {
        return metaAccessors.computeIfAbsent(meta.getClass(), type -> new Accessors(
                findMethod(type, "setId", int.class),
                findMethod(type, "setTimeout", long.class),
                findMethod(type, "getTimeout")
        ));
    }

    private static Method findMethod(Class<?> type, String name, Class<?>... args) {
        try {
            return type.getMethod(name, args);
        } catch (NoSuchMethodException e) {
            throw new InvalidRequestException(e);
        }
    }

    void setId(int id) {
        try {
            accessors.setId.invoke(meta, id);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new InvalidRequestException(e);
        }
    }

    void setTimeout(long timeout) {
        try {
            accessors.setTimeout.invoke(meta, timeout);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new InvalidRequestException(e);
        }
    }

    long getTimeout() {
        try {
            return (long) accessors.getTimeout.invoke(meta);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new InvalidRequestException(e);
        }
    }

}
