package com.cyy.pullrefresh;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author chenyy
 * @date 2020/6/28
 */

public class PullRefreshListView extends LinearLayout implements View.OnTouchListener{
    private static final String TAG = PullRefreshListView.class.getSimpleName();
    private static final int STATUS_PULL_TO_REFRESH = 1;
    private static final int STATUS_RELEASE_TO_REFRESH = 2;
    private static final int STATUS_REFRESHING = 3;
    private static final int STATUS_REFRESH_FINISHED = 4;
    private static int currentStatus = STATUS_REFRESH_FINISHED;
    private static View headerView;
    private TextView refreshTitle;
    private ListView listView;
    private EditText editText;
    private Context context;
    private float lastX;
    private float lastY;
    private boolean mIsBeingDragged = false;
    private boolean init = true;
    private static LayoutParams headerLayoutParams;
    private LayoutParams editTextLp;
    private static int hideHeaderHeight;
    private int editTextWidth;

    public PullRefreshListView(Context context) {
        this(context, null);
    }

    public PullRefreshListView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRefreshListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);
        headerView = LayoutInflater.from(context).inflate(R.layout.header_layout, null, true);
        addView(headerView);
        refreshTitle = headerView.findViewById(R.id.title);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.i(TAG, "onSizeChanged");
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(TAG, "onLayout");
        super.onLayout(changed, l, t, r, b);
        if (init) {
            init = false;
            headerLayoutParams = (LayoutParams) headerView.getLayoutParams();
            hideHeaderHeight = -headerView.getHeight();
            headerLayoutParams.topMargin = hideHeaderHeight;
            Log.i(TAG, "topMargin: " + headerLayoutParams.topMargin);
            headerView.setLayoutParams(headerLayoutParams);
            editText = (EditText) getChildAt(1);
            editTextLp = (LayoutParams) editText.getLayoutParams();
            if (editTextLp.width == ViewGroup.LayoutParams.MATCH_PARENT){
                editTextWidth = getWidth() - editTextLp.leftMargin - editTextLp.rightMargin;
                Log.i(TAG, "editTextWidth: " + editTextWidth);
            } else {
                editTextWidth = editTextLp.width;
            }
            listView = (ListView) getChildAt(2);
            listView.setOnTouchListener(this);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG, "onDraw");
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "ACTION_DOWN");
                lastX = event.getX();
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "ACTION_MOVE");
                int offset = (int) (event.getY() - lastY);
                Log.i(TAG, "offset: " + offset);
                if (isReadyForPull() && offset > 0){
                    if (offset <= 0 && headerLayoutParams.topMargin <= hideHeaderHeight){
                        return false;
                    }
                    if (headerLayoutParams.topMargin >= 0){
                        refreshTitle.setText("释放刷新");
                        currentStatus = STATUS_RELEASE_TO_REFRESH;
                    } else {
                        refreshTitle.setText("下拉刷新");
                        currentStatus = STATUS_PULL_TO_REFRESH;
                    }
                    headerLayoutParams.topMargin = offset/2 + hideHeaderHeight;
                    headerView.setLayoutParams(headerLayoutParams);
                } else {
                    Log.i(TAG, "editText width: " + editTextLp.width);
                    if (editTextLp.width >= 2 * editTextWidth/3 && canNarrow() && offset < 0) {
                        editTextLp.width += -20;
                        editText.setLayoutParams(editTextLp);
                    } else if (editTextLp.width <= editTextWidth && offset > 0){
                        editTextLp.width += 20;
                        editText.setLayoutParams(editTextLp);
                    }
                    return listView.onTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "ACTION_UP");
                if (currentStatus == STATUS_RELEASE_TO_REFRESH){
                    refresh();
                } else if (currentStatus == STATUS_PULL_TO_REFRESH){
                    hide();
                }
                break;
            default:
                break;
        }
        return true;
    }

    private boolean canNarrow() {
        View lastVisibleItemView = listView.getChildAt(listView.getChildCount() - 1);
        return !(lastVisibleItemView != null && lastVisibleItemView.getBottom() == listView.getHeight());
    }

    private boolean canEnlarge(){
        View firstVisibleItemView = listView.getChildAt(0);
        if (firstVisibleItemView.getTop() >= 0 && listView.getTop() >= editText.getHeight()) {
            return false;
        }
        return true;
    }

    private void refresh() {
        /*post(new Runnable() {
            @Override
            public void run() {

            }
        });*/
        final int finalDistance = headerLayoutParams.topMargin;
        ObjectAnimator titleAnimator = ObjectAnimator.ofFloat(refreshTitle, "rotation", 0, 180, 360);
        titleAnimator.setDuration(1000);
        titleAnimator.setInterpolator(new AccelerateInterpolator());
        titleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                new HideHeaderTask().execute();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        titleAnimator.start();
    }

    private void hide() {
        headerLayoutParams.topMargin = hideHeaderHeight;
        headerView.setLayoutParams(headerLayoutParams);
    }

    private boolean isReadyForPull() {
        Adapter adapter = listView.getAdapter();
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        if (null == adapter || adapter.isEmpty()) {
            Log.i(TAG, "Empty View");
            return true;
        }

        if (firstVisiblePosition == 0){
            View firstChild = listView.getChildAt(0);
            if (firstChild != null) {
                Log.i(TAG, "firstChild top: " + firstChild.getTop() + " listView top: " + listView.getTop() + " editText height: " + editText.getHeight());
                return firstChild.getTop() >= 0 && listView.getTop() >= editText.getHeight();
            }
        }
        return false;
    }

    public ListView getListView(){
        return listView;
    }

    static class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            int topMargin = headerLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin - 120;
                if (topMargin <= hideHeaderHeight) {
                    topMargin = hideHeaderHeight;
                    break;
                }
                publishProgress(topMargin);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return topMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            headerLayoutParams.topMargin = topMargin[0];
            headerView.setLayoutParams(headerLayoutParams);
        }

        @Override
        protected void onPostExecute(Integer topMargin) {
            headerLayoutParams.topMargin = topMargin;
            headerView.setLayoutParams(headerLayoutParams);
            currentStatus = STATUS_REFRESH_FINISHED;
        }
    }

}
