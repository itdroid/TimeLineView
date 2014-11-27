package com.sslcs.test;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;
import android.widget.RelativeLayout;

/**
 * Created by CS on 2014/11/24 16:02.
 * TODO :
 */
public class TimeLineView extends RelativeLayout
{
    private final DataSetObserver mObserver = new DataSetObserver()
    {
        @Override
        public void onChanged()
        {
            refreshViewsFromAdapter();
        }

        @Override
        public void onInvalidated()
        {
            removeAllViews();
        }
    };
    private int mColor = 0xffe4e4e4;
    private Paint mPaint = new Paint();
    private int mLineWidth, mMargin, mRadiusDot, mRadiusImage;
    private Adapter mAdapter = null;

    private OnItemClickListener mListener;

    public TimeLineView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mLineWidth = getResources().getDimensionPixelSize(R.dimen.line_width);
        mMargin = getResources().getDimensionPixelSize(R.dimen.margin);
        mRadiusDot = getResources().getDimensionPixelSize(R.dimen.radius_dot);
        mRadiusImage = getResources().getDimensionPixelSize(R.dimen.radius_image);
    }

    public void setAdapter(Adapter adapter)
    {
        if (null != mAdapter)
        {
            mAdapter.unregisterDataSetObserver(mObserver);
        }
        mAdapter = adapter;
        if (null != mAdapter)
        {
            mAdapter.registerDataSetObserver(mObserver);
        }
        initViewsFromAdapter();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isInEditMode())
        {
            return;
        }
        refreshViewsFromAdapter();
    }

    private void initViewsFromAdapter()
    {
        removeAllViews();
        if (null == mAdapter)
        {
            return;
        }

        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++)
        {
            addView(i);
        }
    }

    private void addView(final int pos)
    {
        View view = mAdapter.getView(pos, null, this);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int marginTop = (1 + pos * 2) * mMargin - mRadiusImage;
        int marginLeft = pos % 2 == 0 ? getWidth() / 4 : getWidth() * 3 / 4;
        params.setMargins(marginLeft - mRadiusImage, marginTop, 0, 0);
        addView(view, params);

        if (null == mListener)
        {
            return;
        }
        view.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onItemClick(pos);
            }
        });
    }

    public void setColor(int color)
    {
        mColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (null == mAdapter)
        {
            return;
        }

        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mLineWidth);
        canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), mPaint);

        int margin, count = mAdapter.getCount();
        for (int i = 0; i < count; i++)
        {
            margin = (1 + i * 2) * mMargin;
            canvas.drawCircle(getWidth() / 2, margin, mRadiusDot, mPaint);

            if (i % 2 == 0)
            {
                canvas.drawLine(getWidth() / 4, margin, getWidth() / 2, margin, mPaint);
            }
            else
            {
                canvas.drawLine(getWidth() / 2, margin, getWidth() * 3 / 4, margin, mPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (null == mAdapter)
        {
            return;
        }

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = mAdapter.getCount() * mMargin * 2;
        setMeasuredDimension(sizeWidth, sizeHeight);
    }

    private void refreshViewsFromAdapter()
    {
        int childCount = getChildCount();
        int adapterSize = mAdapter.getCount();
        int reuseCount = Math.min(childCount, adapterSize);

        removeAllViews();
        for (int i = 0; i < reuseCount; i++)
        {
            addView(i);
        }

        if (childCount < adapterSize)
        {
            for (int i = childCount; i < adapterSize; i++)
            {
                addView(i);
            }
        }
        else if (childCount > adapterSize)
        {
            removeViews(adapterSize, childCount);
        }
    }

    public void setOnItemClickListener(OnItemClickListener l)
    {
        mListener = l;
    }

    public interface OnItemClickListener
    {
        public void onItemClick(int pos);
    }
}
