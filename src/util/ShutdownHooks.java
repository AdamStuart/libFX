package util;
/**
 * Copyright 2012 - 2013 Andy Till
 * 
 * This file is part of EstiMate.
 * 
 * EstiMate is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EstiMate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EstiMate.  If not, see <http://www.gnu.org/licenses/>.
 */


import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link List} containing {@link Runnable} instances that could be run on
 * shutdown. Add the following code snippet to have the shutdown hooks execute
 * on shutdown:
 * 
 * <pre>
 * Runtime.getRuntime().addShutdownHook(new Thread() {
 *     &#064;Override
 *     public void run() {
 *         shutdownHooks.runHooks();
 *     }
 * });
 * </pre>
 * The hooks are run synchronously, in reverse order. This means that if one
 * hook is dependant on another then the dependent hook should be added last.
 * <p>
 * For example, if a hypothetical shutdown logger hook relies on a database
 * connection which is shutdown via a hook then the database shutdown hook
 * should be added first and then the shutdown logger hook.
 * <p>
 * <b>{@link ShutdownHooks} is not thread safe<b>
 * 
 * @see <a
 *      href="http://docs.oracle.com/javase/1.4.2/docs/guide/lang/hook-design.html">JVM
 *      Shutdown Hook API Design</a>
 */
@SuppressWarnings("serial")
public class ShutdownHooks extends ArrayList<Runnable> {

    /**
     * Run the hooks in this list in reverse order.
     * 
     * @param handler
     *            An {@link UncaughtExceptionHandler} that will be called when a
     *            hook throws an exception. This can be {@code null}.
     */
    public void runHooks(UncaughtExceptionHandler handler) {
        for (int i = size() - 1; i >= 0; --i) {
            // if one hook fails then do our best to run the others
            try {
                get(i).run();
            } catch (Throwable e) {
                if(handler != null) {
                    notifyHandlerOfException(handler, e);
                }
                else {
                    // print to the output stream as a last resort
                    e.printStackTrace();
                }
            }
        }
    }

    protected void notifyHandlerOfException(UncaughtExceptionHandler handler, Throwable e) {
        // if the handler throws an exception for whatever reason then do our
        // best to carry.  Probably not helpful to notify the handler of it's own
        // exception.
        try {
            handler.uncaughtException(Thread.currentThread(), e);
        } catch (Throwable logExcpetion) {
            logExcpetion.printStackTrace();
        }
    }
}