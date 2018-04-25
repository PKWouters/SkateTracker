package sk8_is_lif3.skatetracker.transitions;

import android.support.transition.ChangeBounds;
import android.support.transition.ChangeTransform;
import android.support.transition.TransitionSet;

public class SessionNameTransition extends TransitionSet {
    public SessionNameTransition() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds()).
                addTransition(new ChangeTransform());
    }
}
