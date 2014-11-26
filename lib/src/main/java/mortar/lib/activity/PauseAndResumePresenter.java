package mortar.lib.activity;

import java.util.HashSet;
import java.util.Set;

import mortar.MortarScope;
import mortar.Presenter;
import mortar.Scoped;

/**
 * Presenter to be registered by the {@link PauseAndResumeActivity}.
 */
public class PauseAndResumePresenter extends Presenter<PauseAndResumeActivity>
    implements PauseAndResumeRegistrar {

  private final Set<Registration> registrations = new HashSet<Registration>();

  public PauseAndResumePresenter() {
  }

  @Override protected MortarScope extractScope(PauseAndResumeActivity view) {
    return view.getMortarScope();
  }

  @Override public void onExitScope() {
    registrations.clear();
  }

  @Override public void register(MortarScope scope, PausesAndResumes listener) {
    Registration registration = new Registration(listener);
    scope.register(registration);

    boolean added = registrations.add(registration);
    if (added && isRunning()) {
      listener.onResume();
    }
  }

  @Override public boolean isRunning() {
    return getView() != null && getView().isRunning();
  }

  public void activityPaused() {
    for (Registration registration : registrations) {
      registration.registrant.onPause();
    }
  }

  public void activityResumed() {
    for (Registration registration : registrations) {
      registration.registrant.onResume();
    }
  }

  private class Registration implements Scoped {
    final PausesAndResumes registrant;

    private Registration(PausesAndResumes registrant) {
      this.registrant = registrant;
    }

    @Override public void onEnterScope(MortarScope scope) {
    }

    @Override public void onExitScope() {
      registrant.onPause();
      registrations.remove(this);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Registration that = (Registration) o;

      return registrant.equals(that.registrant);
    }

    @Override
    public int hashCode() {
      return registrant.hashCode();
    }
  }
}