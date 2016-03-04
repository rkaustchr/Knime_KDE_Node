package org.mkkde;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "KDE" Node.
 * Implementação do Kernel Density Estimation (KDE)
 *
 * @author Rafael Kaustchr
 */
public class KDENodeFactory 
        extends NodeFactory<KDENodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public KDENodeModel createNodeModel() {
        return new KDENodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<KDENodeModel> createNodeView(final int viewIndex,
            final KDENodeModel nodeModel) {
        return new KDENodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new KDENodeDialog();
    }

}

