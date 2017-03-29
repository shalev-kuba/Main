package il.org.spartan.plugin;

import java.lang.reflect.*;
import java.text.*;
import java.util.*;
import java.util.function.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

import il.org.spartan.utils.*;

/** Eclipse common utilities.
 * @author Ori Roth {@code ori.rothh@gmail.com}
 * @since 2017-03-21 */
public class Eclipse {
  private static final boolean REFRESH_OPENS_DIALOG = false;
  /** Height of default tooltips. */
  public static final int TOOLTIP_HEIGHT = 25;

  // TODO Roth: switch from system user to eclipse template user
  /** @return user name */
  public static String user() {
    return System.getProperty("user.name");
  }

  /** @return current date */
  public static String date() {
    return date("dd/MM/yyyy");
  }

  // TODO Roth: switch from system date to eclipse template date
  /** @param format date format
   * @return current date */
  public static String date(final String format) {
    return new SimpleDateFormat(format).format(new Date());
  }

  /** @return current mouse location */
  public static Point mouseLocation() {
    return Optional.ofNullable(Display.getCurrent()) //
        .map(λ -> λ.getCursorLocation()).orElse(new Point(0, 0));
  }

  /** @param mouseUp mouse up operation
   * @param mouseDown mouse down operation
   * @param mouseDoubleClick mouse double click operation
   * @return a {@link MouseListener} that does those actions */
  public static MouseListener mouseListener(final Consumer<MouseEvent> mouseUp, final Consumer<MouseEvent> mouseDown,
      final Consumer<MouseEvent> mouseDoubleClick) {
    return new MouseListener() {
      @Override public void mouseUp(final MouseEvent ¢) {
        mouseUp.accept(¢);
      }

      @Override public void mouseDown(final MouseEvent ¢) {
        mouseDown.accept(¢);
      }

      @Override public void mouseDoubleClick(final MouseEvent ¢) {
        mouseDoubleClick.accept(¢);
      }
    };
  }

  /** Refreshes project, while applying new configuration.
   * @param p JD
   * @throws CoreException
   * @throws InvocationTargetException
   * @throws InterruptedException */
  public static void refreshProject(final IProject p) throws CoreException, InvocationTargetException, InterruptedException {
    if (p != null && p.isOpen() && p.getNature(Nature.NATURE_ID) != null)
      if (!REFRESH_OPENS_DIALOG)
        new Job("Refreshing " + p.getName()) {
          @Override protected IStatus run(final IProgressMonitor m) {
            try {
              p.build(IncrementalProjectBuilder.FULL_BUILD, m);
              return Status.OK_STATUS;
            } catch (final CoreException ¢) {
              monitor.log(¢);
              return Status.CANCEL_STATUS;
            }
          }
        }.schedule();
      else {
        final ProgressMonitorDialog d = Dialogs.progress(true);
        d.run(true, true, m -> {
          SpartanizationHandler.runAsynchronouslyInUIThread(() -> {
            final Shell s = d.getShell();
            if (s != null)
              s.setText("Refreshing project");
          });
          try {
            p.build(IncrementalProjectBuilder.FULL_BUILD, m);
          } catch (final CoreException ¢) {
            monitor.log(¢);
          }
        });
      }
  }
}
