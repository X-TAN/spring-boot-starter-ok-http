
package org.xtan.ok.http.utils;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 对 {@link java.util.Optional} 的增强类
 *
 * @author: X-TAN
 * @date: 2021-08-25
 */
public final class XOptional<T> {
    /**
     * 提供一个默认的空对象
     */
    private static final XOptional<?> EMPTY = new XOptional<>();

    /**
     * 需要用来进行非空判断的值
     */
    private final T value;

    /**
     * 私有化构造方法
     */
    private XOptional() {
        this.value = null;
    }

    private XOptional(T value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * 创建一个空的XOptional
     *
     * @return XOptional<T>
     */
    public static <T> XOptional<T> empty() {
        @SuppressWarnings("unchecked")
        XOptional<T> t = (XOptional<T>) EMPTY;
        return t;
    }

    /**
     * 直接接受一个值，如果值为空，会抛出 {@link NullPointerException} 异常
     *
     * @param value 值
     * @return XOptional<T>
     */
    public static <T> XOptional<T> of(T value) {
        return new XOptional<>(value);
    }


    /**
     * 接受一个值，如果该值为空，则返回一个空对象，而不是抛出 {@link NullPointerException}
     *
     * @param value 值
     * @return XOptional<T>
     */
    public static <T> XOptional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }


    /**
     * 获取值，如果值为空会抛出 {@link NoSuchElementException}
     *
     * @return T
     */
    public T end() {
        return value;
    }

    /**
     * 如果值不为空，执行一个消费者，做一些事情
     *
     * @param consumer 消费者
     * @return XOptional<T>
     */
    public XOptional<T> ifPresent(Consumer<? super T> consumer) {
        if (value != null) consumer.accept(value);
        return this;
    }

    /**
     * 如果值为空，执行一段代码逻辑，做一些事情
     *
     * @param runnable 代码片段
     * @return XOptional<T>
     */
    public XOptional<T> notPresent(Runnable runnable) {
        if (value == null) runnable.run();
        return this;
    }

    /**
     * 过滤器，如果该方法返回为 true 则会返回一个空的实例
     *
     * @param predicate 过滤条件
     * @return
     */
    public XOptional<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (value == null)
            return this;
        else
            return predicate.test(value) ? this : empty();
    }


    /**
     * 转换器，将值转换为另一种类型
     *
     * @param mapper 转换代码片段
     * @return
     */
    public <U> XOptional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (value == null)
            return empty();
        else return new XOptional<>(mapper.apply(value));
    }

    /**
     * 如果值不为空，则返回值，为空则返回传入的参数
     *
     * @param other 其他值
     * @return
     */
    public T orElse(T other) {
        return value != null ? value : other;
    }

    /**
     * 如果值不为空，则返回值，否则返回一个代码片段的返回结果
     *
     * @param other 事件结果值
     * @return
     */
    public T orElseGet(Supplier<? extends T> other) {
        return value != null ? value : other.get();
    }

    /**
     * 如果值为空则抛出异常，不为空则返回值
     *
     * @param throwable 传入一个异常
     * @throws X 如果值为空则抛出传入的异常
     */
    public <X extends Throwable> XOptional<T> orElseThrow(X throwable) throws X {
        if (value == null) throw throwable;
        return this;
    }

    /**
     * 转换为java的Optional
     *
     * @return
     */
    public Optional toJavaOptional() {
        return Optional.ofNullable(value);
    }

    public <U> XOptional<U> flatMap(Function<? super T, XOptional<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (value == null)
            return empty();
        else {
            return Objects.requireNonNull(mapper.apply(value));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof XOptional)) {
            return false;
        }

        XOptional<?> other = (XOptional<?>) obj;
        return Objects.equals(value, other.value);
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }


    @Override
    public String toString() {
        return value != null
                ? String.format("XOptional[%s]", value)
                : "XOptional.empty";
    }
}
