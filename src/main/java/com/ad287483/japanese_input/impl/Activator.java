package com.ad287483.japanese_input.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import com.ur.urcap.api.contribution.toolbar.swing.SwingToolbarService;

/**
 * Hello world activator for the OSGi bundle URCAPS contribution
 *
 */
public class Activator implements BundleActivator {
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		bundleContext.registerService(SwingToolbarService.class, new JapaneseInputToolbarService(), null);
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
	}
}

