/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.habmath;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.android.common.logger.Log;


/**
 * Simple Fragment used to display some meaningful content for each page in the sample's
 * {@link android.support.v4.view.ViewPager}.
 */
public class ContentFragment extends Fragment {

    private static final String KEY_TITLE = "title";
    private static final String KEY_INDICATOR_COLOR = "indicator_color";
    private static final String KEY_DIVIDER_COLOR = "divider_color";
    private static final String KEY_RADIUS_FACTOR = "radius_factor";
    private static final String KEY_EFFECTIVE_G = "effective_g";
    private static final String KEY_RPM = "rpm";
    private static final String KEY_LINEAR_ROT = "linear";

    /**
     * @return a new instance of {@link ContentFragment}, adding the parameters into a bundle and
     * setting them as arguments.
     */
    public static ContentFragment newInstance(CharSequence title, int indicatorColor,
            int dividerColor) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(KEY_TITLE, title);
        bundle.putInt(KEY_INDICATOR_COLOR, indicatorColor);
        bundle.putInt(KEY_DIVIDER_COLOR, dividerColor);
        bundle.putInt(KEY_RADIUS_FACTOR, 100);
        bundle.putDouble(KEY_EFFECTIVE_G, 1.0);
        bundle.putDouble(KEY_RPM, 0.0);
        bundle.putDouble(KEY_LINEAR_ROT, 0.0);

        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pager_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            TextView radiusText = (TextView) view.findViewById(R.id.radiusText);
            SeekBar slider = (SeekBar) view.findViewById(R.id.seekBar);
            TextView efgText = (TextView) view.findViewById(R.id.efgText);
            int radius = args.getInt(KEY_RADIUS_FACTOR);

            slider.setProgress(radius / 100);
            radiusText.setText(String.valueOf(radius));
            radiusText.clearFocus();

            // perform seek bar change listener event used for getting the progress value
            slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChangedValue = 0;

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChangedValue = progress;
                    //Log.i("SLIDER", String.format("Radius changing to %d", progressChangedValue));
                    View v = seekBar.getRootView();
                    TextView rt  = (TextView) v.findViewById(R.id.radiusText);
                    rt.setText(String.valueOf(progressChangedValue * 100));
                    Bundle a = getArguments();
                    a.putInt(KEY_RADIUS_FACTOR, progressChangedValue * 100);
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    Log.i("SLIDER", String.format("Radius is %d", progressChangedValue));
                    View v = seekBar.getRootView();
                    TextView rt  = (TextView) v.findViewById(R.id.radiusText);
                    TextView e = (TextView) v.findViewById(R.id.efgText);
                    rt.setText(String.valueOf(progressChangedValue * 100));
                    e.setText("1.0");
                    Bundle a = getArguments();
                    a.putInt(KEY_RADIUS_FACTOR, progressChangedValue * 100);
                    a.putDouble(KEY_EFFECTIVE_G, 1.0);

                    // Calculate
                    calcRPM(v, a);

                    rt.clearFocus();
                }
            });

            efgText.setText(String.valueOf(1.0));

            slider.setBackgroundColor(args.getInt(KEY_INDICATOR_COLOR));

            Button efgBtn = (Button) view.findViewById(R.id.efgLabel);
            efgBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Bundle a = getArguments();

                    calcEFG(v.getRootView(), a);
                }
            });

            /*
            TextView title = (TextView) view.findViewById(R.id.item_title);
            title.setText("Title: " + args.getCharSequence(KEY_TITLE));

            int indicatorColor = args.getInt(KEY_INDICATOR_COLOR);
            TextView indicatorColorView = (TextView) view.findViewById(R.id.item_indicator_color);
            indicatorColorView.setText("Indicator: #" + Integer.toHexString(indicatorColor));
            indicatorColorView.setTextColor(indicatorColor);

            int dividerColor = args.getInt(KEY_DIVIDER_COLOR);
            TextView dividerColorView = (TextView) view.findViewById(R.id.item_divider_color);
            dividerColorView.setText("Divider: #" + Integer.toHexString(dividerColor));
            dividerColorView.setTextColor(dividerColor);
            */
        }
    }

    private void calcEFG(View v, Bundle a) {
        TextView rpmText = (TextView) v.findViewById(R.id.rpmText);
        TextView linearText = (TextView) v.findViewById(R.id.linearText);
        TextView radiusText = (TextView) v.findViewById(R.id.radiusText);
        TextView efgText = (TextView) v.findViewById(R.id.efgText);
        double radius = 0;
        double rpm = 0;
        double rads = 0;
        double efg = 0;
        double linear = 0;
        String tmp = "";

        try {
            tmp = radiusText.getText().toString();
            radius = Double.parseDouble(tmp); // m
            a.putInt(KEY_RADIUS_FACTOR, (int) radius);
        } catch (Exception e) {
            Log.e("CALCULATE", "Bad string: " + tmp );
        }
        try {
            tmp = rpmText.getText().toString();
            rpm = Double.parseDouble(tmp);
            a.putDouble(KEY_RPM, rpm);
        } catch (Exception e) {
            Log.e("CALCULATE", "Bad string: " + tmp );
        }

        Log.i("CALCULATE", String.format("radius = %.4f, rpm = %.4f", radius, rpm));

        // rpm to rads: rpm / (2.0 * Math.PI * 60.0);
        rads = (rpm * 2.0 * Math.PI) / 60.0;
        // a = rads^2 / r
        efg = (rads * rads) * radius; // m/s^2
        Log.i("CALCULATE", String.format("Effective acceleration = %.4f m/s^2", efg));
        linear = Math.sqrt(efg * radius);
        efg = efg / 9.807; // to g
        a.putDouble(KEY_EFFECTIVE_G, efg);
        efgText.setText(String.format("%.1f", efg));

        Log.i("CALCULATE", String.format("Linear rotation speed is %f m/s", linear));
        // m/s to mph is 1 m/s = 2.237 mph
        linear = linear * 2.237;
        linearText.setText(String.format("%.2f", linear));
        a.putDouble(KEY_LINEAR_ROT, linear);
        Log.i("CALCULATE", String.format("Linear rotation speed is %f mph", linear));
    }

    private void calcRPM(View v, Bundle a) {
        TextView rpmText = (TextView) v.findViewById(R.id.rpmText);
        TextView linearText = (TextView) v.findViewById(R.id.linearText);
        TextView radiusText = (TextView) v.findViewById(R.id.radiusText);
        TextView efgText = (TextView) v.findViewById(R.id.efgText);
        double radius = 0;
        double rpm = 0;
        double rads = 0;
        double efg = 0;
        double linear = 0;
        String tmp = "";

        radius = a.getInt(KEY_RADIUS_FACTOR); // m
        efg = a.getDouble(KEY_EFFECTIVE_G) * 9.807; // m/s^2
        rads = Math.sqrt(efg / radius); // rads/s

        Log.i("CALCULATE", String.format("Radians per sec is %f", rads));
        // 360 degrees = 2 pi radians = 1 revolution
        rpm = (rads * 60.0) / (2.0 * Math.PI);
        a.putDouble(KEY_RPM, rpm);
        rpmText.setText(String.format("%.4f", rpm));

        Log.i("CALCULATE", String.format("RPM is %f", rpm));

        linear = Math.sqrt(efg * radius);
        Log.i("CALCULATE", String.format("Linear rotation speed is %f m/s", linear));
        // m/s to mph is 1 m/s = 2.237 mph
        linear = linear * 2.237;
        linearText.setText(String.format("%.2f", linear));
        a.putDouble(KEY_LINEAR_ROT, linear);
        Log.i("CALCULATE", String.format("Linear rotation speed is %f mph", linear));
    }
}
