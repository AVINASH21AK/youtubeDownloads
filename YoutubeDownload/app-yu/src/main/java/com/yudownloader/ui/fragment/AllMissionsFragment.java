package com.yudownloader.ui.fragment;

import com.yudownloader.get.DownloadManager;
import com.yudownloader.service.DownloadManagerService;

public class AllMissionsFragment extends MissionsFragment
{

	@Override
	protected DownloadManager setupDownloadManager(DownloadManagerService.DMBinder binder) {
		return binder.getDownloadManager();
	}
}
