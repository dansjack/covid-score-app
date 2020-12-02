package com.nsc.covidscore;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.appcompat.widget.AppCompatTextView;

import static android.widget.TextView.BufferType.NORMAL;

/**
 * Class comes from A Medium article by Yuriy Skul
 * https://medium.com/@yuriyskul/expandable-textview-using-staticlayouts-data-f9bc9cbdf283
 */
public class ExpandableTextView extends AppCompatTextView
        implements View.OnClickListener {

    private final int COLLAPSED_MAX_LINES = 3;
    private final static String POSTFIX = "...see more ";

    private ValueAnimator mAnimator;
    private boolean isCollapsing;
    private CharSequence mOriginalText;

//    public ExpandableTextView(Context context) {
//        super(context);
//        init();
//    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

//    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init();
//    }

    private void init() {
        setMaxLines(COLLAPSED_MAX_LINES);
        setOnClickListener(this);
        initAnimator();
    }


    private void initAnimator() {
        mAnimator = ValueAnimator.ofInt(-1, -1)
                .setDuration(450);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                updateHeight((int) valueAnimator.getAnimatedValue());
            }
        });

        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (isCollapsed()) {
                    isCollapsing = false;
                    setMaxLines(Integer.MAX_VALUE);
                    deEllipsize();           //add this line
                } else {
                    isCollapsing = true;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isCollapsed() && isCollapsing) {
                    setMaxLines(COLLAPSED_MAX_LINES);
                    ellipsizeColored();      // add this line
                    isCollapsing = false;
                }
                setWrapContent();
            }
        });
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        mOriginalText = text;
        super.setText(text, type);
    }

    private void ellipsizeColored() {
        int end = getLayout().getLineEnd(COLLAPSED_MAX_LINES - 1);
        CharSequence text = getText();

        int chars = getLayout().getLineEnd(COLLAPSED_MAX_LINES - 1)
                - getLayout().getLineStart(COLLAPSED_MAX_LINES - 1);

        int additionalGap = 4;
        if (chars + additionalGap < POSTFIX.length()) {
            // handle rare case when text has a last  maxLine which has  only few chars and
            // then it goes to the next line .
            // lin such case there is nothing twe cannot replace because postfix
            // length is greater then max line length. Do nothing.
            return;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        builder.replace(end - POSTFIX.length(), end, POSTFIX);
        builder.setSpan(new ForegroundColorSpan(Color.BLACK),
                end - POSTFIX.length(), end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setTextNoCaching(builder);
    }

    private void deEllipsize() {
        super.setText(mOriginalText);
    }

    public void setTextNoCaching(CharSequence text) {
        super.setText(text, NORMAL);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getLineCount() <= COLLAPSED_MAX_LINES) {
            deEllipsize();  // add to fix current bug
            setClickable(false);
        } else {
            setClickable(true);
            if (!mAnimator.isRunning() && isCollapsed()) {
                ellipsizeColored();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (mAnimator.isRunning()) {
            animatorReverse();
            return;
        }

        int endPosition = animateTo();
        int startPosition = getHeight();

        mAnimator.setIntValues(startPosition, endPosition);
        animatorStart();
    }

    private void animatorReverse() {
        isCollapsing = !isCollapsing;
        mAnimator.reverse();
    }

    private void animatorStart() {
        mAnimator.start();
    }

    private int animateTo() {
        if (isCollapsed()) {
            return getLayout().getHeight() + getPaddingHeight();
        } else {
            return getLayout().getLineBottom(COLLAPSED_MAX_LINES - 1)
                    + getLayout().getBottomPadding() + getPaddingHeight();
        }
    }

    private int getPaddingHeight() {
        return getCompoundPaddingBottom() + getCompoundPaddingTop();
    }

    private boolean isCollapsed() {
        return Integer.MAX_VALUE != getMaxLines();
    }

    private void updateHeight(int animatedValue) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = animatedValue;
        setLayoutParams(layoutParams);
    }

    private void setWrapContent() {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        setLayoutParams(layoutParams);
    }
}