package sca.biwiwatchface;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.format.Time;
import android.view.WindowInsets;

import java.util.Locale;

public class TimePaint {
    private static final String COLON_STRING = ":";
    private static final String DIGIT_STRING = "00";

    private Context mContext;

    private Paint mHourPaint;
    private Paint mMinutePaint;
    private Paint mColonPaint;

    private float mColonHalfWidth;
    private float mColonHalfHeight;
    private float mDigitHalfHeight;

    public TimePaint(Context context) {
        mContext = context;
        Resources resources = mContext.getResources();
        Resources.Theme theme = mContext.getTheme();
        int textColor = resources.getColor(R.color.digital_time, theme );
        Typeface typefaceMedium = Typeface.create("sans-serif-medium", Typeface.NORMAL);

        mHourPaint = new Paint();
        mHourPaint.setColor(textColor);
        mHourPaint.setTypeface(typefaceMedium);
        mHourPaint.setTextAlign(Paint.Align.RIGHT);

        mMinutePaint = new Paint();
        mMinutePaint.setColor(textColor);
        mMinutePaint.setTypeface(typefaceMedium);
        mMinutePaint.setTextAlign(Paint.Align.LEFT);

        Typeface typefaceNormal = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
        mColonPaint = new Paint();
        mColonPaint.setColor(textColor);
        mColonPaint.setTypeface(typefaceNormal);
        mColonPaint.setTextAlign(Paint.Align.CENTER);

        setAntiAlias(true);
    }

    public void onApplyWindowInsets(WindowInsets insets) {
        Resources resources = mContext.getResources();
        boolean isRound = insets.isRound();
        float textSize = resources.getDimension(isRound ? R.dimen.digital_time_size_round : R.dimen.digital_time_size);
        mHourPaint.setTextSize(textSize);
        mMinutePaint.setTextSize(textSize);
        mColonPaint.setTextSize(textSize);

        Rect rcBounds = new Rect();
        mColonPaint.getTextBounds(COLON_STRING, 0, COLON_STRING.length(), rcBounds );
        mColonHalfWidth = rcBounds.width();
        mColonHalfHeight = rcBounds.height() / 2.0f;

        mMinutePaint.getTextBounds(DIGIT_STRING, 0, DIGIT_STRING.length(), rcBounds );
        mDigitHalfHeight = rcBounds.height() / 2.0f;
    }

    public void setAntiAlias(boolean enabled) {
        mHourPaint.setAntiAlias(enabled);
        mMinutePaint.setAntiAlias(enabled);
        mColonPaint.setAntiAlias(enabled);
    }

    public void drawTime(Canvas canvas, Time time, int x, int y, boolean ambient) {
        String szHour = Integer.toString(time.hour);
        String szMinute = String.format(Locale.US, "%02d", time.minute);

        float hourWidth = mHourPaint.measureText(szHour);
        float minuteWidth = mMinutePaint.measureText(szMinute);
        float hmDeltaWidth = hourWidth - minuteWidth;
        float xColonPos = x + hmDeltaWidth/2;
        float yDigit = y + mDigitHalfHeight;

        canvas.drawText( szHour, xColonPos - mColonHalfWidth, yDigit, mHourPaint );
        if ( ambient || time.second % 2 == 0 ) {
            canvas.drawText(COLON_STRING, xColonPos, y + mColonHalfHeight, mColonPaint);
        }

        canvas.drawText( szMinute, xColonPos + mColonHalfWidth, yDigit, mMinutePaint );
    }
}
