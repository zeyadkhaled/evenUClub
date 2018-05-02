package com.bilkentazure.evenuclub.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bilkentazure.evenuclub.fragments.AddEventFragment;
import com.bilkentazure.evenuclub.fragments.HomeFragment;
import com.bilkentazure.evenuclub.fragments.ProfileFragment;

public class PagerAdapter extends FragmentPagerAdapter {

	public PagerAdapter(FragmentManager fragmentManager){
		super(fragmentManager);
	}

	@Override
	public Fragment getItem(int position) {

		switch (position){
			case 0:
				HomeFragment homeFragment = new HomeFragment();
				return homeFragment;
			case 1:
				AddEventFragment addEventFragment = new AddEventFragment();
				return addEventFragment;
            case 2:
                ProfileFragment profileFragment = new ProfileFragment();
                return profileFragment;
			default:
				return null;
		}
	}

	@Override
	public int getCount() {
		return 3;
	}




}
