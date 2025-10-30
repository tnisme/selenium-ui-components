package com.seleniumui.executors;

import java.util.function.Supplier;

public class RetryExecutor {

    private static final int DEFAULT_RETRY_COUNT = 3;
    private static final long DEFAULT_DELAY_MS = 1000;

    private RetryExecutor() {}

    public static void runWithRetry(Runnable action, int retryCount, long delayMs) {
        for (int i = 0; i < retryCount; i++) {
            try {
                action.run();
                System.out.println("Action succeeded on attempt " + (i + 1));
                return;
            } catch (Exception e) {
                if (i == retryCount - 1) throw e;
                sleep(delayMs);
                System.out.println("Action failed on attempt " + (i + 1) + ", retrying...");
            }
        }
    }

    public static void runWithRetry(Runnable action) {
        runWithRetry(action, DEFAULT_RETRY_COUNT, DEFAULT_DELAY_MS);
    }

    public static <T> T getWithRetry(Supplier<T> supplier, int retryCount, long delayMs) {
        for (int i = 0; i < retryCount; i++) {
            try {
                return supplier.get();
            } catch (Exception e) {
                if (i == retryCount - 1) throw e;
                sleep(delayMs);
            }
        }
        throw new IllegalStateException("Retry exhausted");
    }

    public static <T> T getWithRetry(Supplier<T> supplier) {
        return getWithRetry(supplier, DEFAULT_RETRY_COUNT, DEFAULT_DELAY_MS);
    }

    private static void sleep(long delayMs) {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
