/*
 * Copyright 2019 Chaos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.chaos.fx.cnbeta.theme;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;


public class TimePickerDialogFragment extends DialogFragment {

    private static final String KEY_HOUR_OF_DAY = "hour_of_day";
    private static final String KEY_MINUTE = "minute";

    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

    public static TimePickerDialogFragment newInstance(int hourOfDay, int minute) {
        Bundle args = new Bundle();
        args.putInt(KEY_HOUR_OF_DAY, hourOfDay);
        args.putInt(KEY_MINUTE, minute);
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setTimeSetListener(TimePickerDialog.OnTimeSetListener timeSetListener) {
        mTimeSetListener = timeSetListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour = 0;
        int minute = 0;
        if (getArguments() != null) {
            hour = getArguments().getInt(KEY_HOUR_OF_DAY);
            minute = getArguments().getInt(KEY_MINUTE);
        }
        return new TimePickerDialog(getActivity(), mTimeSetListener, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

}
