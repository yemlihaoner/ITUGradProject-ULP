package Utils;
import java.util.concurrent.*;

//Timeout function to create runnable code pieces on ObjectInputStreams.
public class TimeOutRunner {
        public static void runWithTimeout(final Runnable runnable, long timeout, TimeUnit timeUnit) throws Exception {
            runWithTimeout(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    runnable.run();
                    return null;
                }
            }, timeout, timeUnit);
        }

        public static <T> T runWithTimeout(Callable<T> callable, long timeout, TimeUnit timeUnit) throws Exception {
            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Future<T> future = executor.submit(callable);
            executor.shutdown();
            try {
                return future.get(timeout, timeUnit);
            }
            catch (TimeoutException e) {
                future.cancel(true);
                throw e;
            }
            catch (ExecutionException e) {
                //unwrap the root cause
                Throwable t = e.getCause();
                if (t instanceof Error) {
                    throw (Error) t;
                } else if (t instanceof Exception) {
                    throw (Exception) t;
                } else {
                    throw new IllegalStateException(t);
                }
            }
        }

    }