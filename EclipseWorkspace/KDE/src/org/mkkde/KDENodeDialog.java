package org.mkkde;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "KDE" Node.
 * Implementação do Kernel Density Estimation (KDE)
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Rafael Kaustchr
 */
public class KDENodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring KDE node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected KDENodeDialog() {
        super();
        
        addDialogComponent(new DialogComponentStringSelection(
        		new SettingsModelString(KDENodeModel.CFGKEY_KERNEL, KDENodeModel.DEFAULT_KERNEL),
        		"Select the kernel function",
        		KDENodeModel.possibleKernelValues));
    }
}

