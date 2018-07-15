package com.benio.asnackbar;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.AnimatedTransientBar;
import android.support.design.widget.BaseTransientBottomBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by benio on 2018/7/14.
 */
public class FakeToast extends AnimatedTransientBar<FakeToast> {
    /**
     * Show the Toast indefinitely. This means that the Toast will be displayed from the time
     * that is {@link #show() shown} until either it is dismissed, or another Toast is shown.
     *
     * @see #setDuration
     */
    public static final int LENGTH_INDEFINITE = BaseTransientBottomBar.LENGTH_INDEFINITE;

    /**
     * Show the Toast for a short period of time.
     *
     * @see #setDuration
     */
    public static final int LENGTH_SHORT = BaseTransientBottomBar.LENGTH_SHORT;

    /**
     * Show the Toast for a long period of time.
     *
     * @see #setDuration
     */
    public static final int LENGTH_LONG = BaseTransientBottomBar.LENGTH_LONG;

    /**
     * Callback class for {@link FakeToast} instances.
     * <p>
     * Note: this class is here to provide backwards-compatible way for apps written before
     * the existence of the base {@link BaseTransientBottomBar} class.
     *
     * @see BaseTransientBottomBar#addCallback(BaseCallback)
     */
    public static class Callback extends BaseCallback<FakeToast> {
        /**
         * Indicates that the FakeToast was dismissed via a swipe.
         */
        public static final int DISMISS_EVENT_SWIPE = BaseCallback.DISMISS_EVENT_SWIPE;
        /**
         * Indicates that the FakeToast was dismissed via an action click.
         */
        public static final int DISMISS_EVENT_ACTION = BaseCallback.DISMISS_EVENT_ACTION;
        /**
         * Indicates that the FakeToast was dismissed via a timeout.
         */
        public static final int DISMISS_EVENT_TIMEOUT = BaseCallback.DISMISS_EVENT_TIMEOUT;
        /**
         * Indicates that the FakeToast was dismissed via a call to {@link #dismiss()}.
         */
        public static final int DISMISS_EVENT_MANUAL = BaseCallback.DISMISS_EVENT_MANUAL;
        /**
         * Indicates that the FakeToast was dismissed from a new FakeToast being shown.
         */
        public static final int DISMISS_EVENT_CONSECUTIVE = BaseCallback.DISMISS_EVENT_CONSECUTIVE;

        @Override
        public void onShown(FakeToast toast) {
            // Stub implementation to make API check happy.
        }

        @Override
        public void onDismissed(FakeToast toast, @DismissEvent int event) {
            // Stub implementation to make API check happy.
        }
    }

    private TextView mMessageView;

    private FakeToast(@NonNull ViewGroup parent, @NonNull View content,
                      @NonNull ContentViewCallback contentViewCallback) {
        super(parent, content, contentViewCallback);

        final View view = getView();
        view.setBackgroundColor(Color.TRANSPARENT);
        setAnimationEnabled(false);

        final ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        if (lp instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) lp).gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        }
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            final Resources resources = view.getResources();
            final int yOffsetDimenId = resources.getIdentifier("toast_y_offset",
                    "dimen", "android");
            final int yOffset = resources.getDimensionPixelSize(yOffsetDimenId);
            ((ViewGroup.MarginLayoutParams) lp).bottomMargin = yOffset;
        }
        view.setLayoutParams(lp);
    }

    public static FakeToast make(@NonNull View view, @NonNull CharSequence text,
                                 @Duration int duration) {
        final ViewGroup parent = findSuitableParent(view);
        if (parent == null) {
            throw new IllegalArgumentException("No suitable parent found from the given view. "
                    + "Please provide a valid view.");
        }

        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final int toastLayout = parent.getResources().getIdentifier("transient_notification",
                "layout", "android");
        final View content = inflater.inflate(toastLayout, parent, false);
        final ContentViewCallback callback = new ContentViewCallback() {
            @Override
            public void animateContentIn(int delay, int duration) {
            }

            @Override
            public void animateContentOut(int delay, int duration) {
            }
        };
        final FakeToast toast = new FakeToast(parent, content, callback);
        toast.mMessageView = (TextView) content.findViewById(android.R.id.message);
        toast.setText(text);
        toast.setDuration(duration);
        return toast;
    }

    @NonNull
    public static FakeToast make(@NonNull View view, @StringRes int resId, @Duration int duration) {
        return make(view, view.getResources().getText(resId), duration);
    }

    private static ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;
        do {
            // TODO we are a toast, so we should disable features in CoordinatorLayout
            /*if (view instanceof CoordinatorLayout) {
                // We've found a CoordinatorLayout, use it
                return (ViewGroup) view;
            } else */
            if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return (ViewGroup) view;
                } else {
                    // It's not the content view but we'll use it as our fallback
                    fallback = (ViewGroup) view;
                }
            }

            if (view != null) {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                final ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback;
    }

    /**
     * Update the text in this {@link FakeToast}.
     *
     * @param message The new text for this {@link BaseTransientBottomBar}.
     */
    @NonNull
    public FakeToast setText(@NonNull CharSequence message) {
        final TextView tv = mMessageView;
        tv.setText(message);
        return this;
    }

    /**
     * Update the text in this {@link FakeToast}.
     *
     * @param resId The new text for this {@link BaseTransientBottomBar}.
     */
    @NonNull
    public FakeToast setText(@StringRes int resId) {
        return setText(getContext().getText(resId));
    }
}