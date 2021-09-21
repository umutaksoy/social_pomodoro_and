package com.sosimple.furtle.models;

import android.widget.LinearLayout;
import android.widget.TextView;

import ru.nikartm.support.ImageBadgeView;

public class UserViews {
    public LinearLayout userLayout;
    public ImageBadgeView userProfileImage;
    public TextView userNameText;

    public UserViews(LinearLayout layout, ImageBadgeView badge, TextView text){
        this.userLayout = layout;
        this.userProfileImage = badge;
        this.userNameText = text;
    }

}
