package org.mkkde;

import java.io.File;
import java.io.IOException;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnDomainCreator;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;


/**
 * This is the model implementation of KDE.
 *
 * @author Rafael Kaustchr
 */
public class KDENodeModel extends NodeModel {
    
    // the logger instance
    @SuppressWarnings("unused")
	private static final NodeLogger logger = NodeLogger
            .getLogger(KDENodeModel.class);
    
    /**
     * Input ports
     * 		testingMatrix: input matrix with the testing values, where the KDE will by applied.
	 *      trainingMatrix: input matrix with the training value, where KDE will get the PFD.
     * 
     * Output ports
     * 		P: the vector p with the calculated KDE for each variable of testingMatrix
     */
    // IN
    public static final int IN_PORT_TRAINING = 0;
    public static final int IN_PORT_TESTING = 1;
    public static final int IN_PORT_BANDWIDTH = 2;
    
    // OUT
    public static final int OUT_PORT_P = 0;

    /** the settings key which is used to retrieve and 
        store the settings (from the dialog or from a settings file)    
       (package visibility to be usable from the dialog). */
	static final String CFGKEY_KERNEL = "Kernel";

    /** initial default kernels values. */
	static final String[] possibleKernelValues = { "gaussian", "epanechnikov" };
    static final String DEFAULT_KERNEL = "gaussian";

    /**
     * example value: the models count variable filled from the dialog 
     * and used in the models execution method. The default components of the
     * dialog work with "SettingsModels".
    */
    private final SettingsModelString m_kernel =
        new SettingsModelString( KDENodeModel.CFGKEY_KERNEL, KDENodeModel.DEFAULT_KERNEL);


    /**
     * Constructor for the node model.
     */
    protected KDENodeModel() {
        // three incoming ports and one outgoing port is assumed
        super(3, 1);
    }

    /**
     * {@inheritDoc}
     * 
     * Method to execute the KDE function
     * 
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	
    	int d = inData[IN_PORT_TRAINING].getDataTableSpec().getNumColumns(); // Dimensions
    	double testingMatrix[][] = new double[ inData[IN_PORT_TESTING].getRowCount() ][ d ];
    	int m = inData[IN_PORT_TRAINING].getRowCount();  // Size of training
        double trainingMatrix[][] = new double[ inData[IN_PORT_TRAINING].getRowCount() ][ d ];
        int n = inData[IN_PORT_TESTING].getRowCount();  // Size of testing elements
        
        double bandwidth[] = new double[ inData[IN_PORT_BANDWIDTH].getRowCount() ];
        
        
        if ( inData[IN_PORT_BANDWIDTH].getRowCount() != d ) {
        	throw new InvalidSettingsException("Row's Number of input table 'bandwidth' must be the same as table 'training/testing' columns");
        }
        
        // TODO do something here
        //logger.info("Node Model Stub... this is in development !");
        
        // Load training table
        BufferedDataTable tblTraining = inData[IN_PORT_TRAINING];
        int currentRow = 0;
        for (DataRow row : tblTraining) {
            // check if the user cancelled the execution
            exec.checkCanceled();
            // report progress
            exec.setProgress((double)currentRow / m, " processing row " + currentRow + " from training table");
            for (int currentColumn = 0; currentColumn < d; currentColumn++) {
            	exec.checkCanceled();
            	trainingMatrix[currentRow][currentColumn] = Double.parseDouble( row.getCell(currentColumn).toString() );
            } 
            currentRow++;
        }
        
        // Load testing table
        BufferedDataTable tblTesting = inData[IN_PORT_TESTING];
        currentRow = 0;
        for (DataRow row : tblTesting) {
            // check if the user cancelled the execution
            exec.checkCanceled();
            // report progress
            exec.setProgress((double)currentRow / n, " processing row " + currentRow + " from testing table");
            for (int currentColumn = 0; currentColumn < d; currentColumn++) {
            	exec.checkCanceled();
            	testingMatrix[currentRow][currentColumn] = Double.parseDouble( row.getCell(currentColumn).toString() );
           }       
           currentRow++;
        }
        
        // Load bandwidth table
        BufferedDataTable tblH = inData[IN_PORT_BANDWIDTH];
        currentRow = 0;
        for (DataRow linha : tblH) {
            // check if the user cancelled the execution
            exec.checkCanceled();
            // report progress
            exec.setProgress((double)currentRow / d, " processing row " + currentRow + " from bandwidth table");

            bandwidth[currentRow] = Double.parseDouble( linha.getCell(0).toString() );
                  
            currentRow++;
        }
               
        
        // KDE
        KDE kde = new KDE(m_kernel.getStringValue(), exec);
        double p[][] = kde.calculate(testingMatrix, trainingMatrix, bandwidth);
        
        
        // the data table spec of the single output table
        DataColumnSpec[] allColSpecs = new DataColumnSpec[1];
        allColSpecs[0] = new DataColumnSpecCreator("KDE(x)", DoubleCell.TYPE).createSpec();

        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
        BufferedDataContainer containerOut = exec.createDataContainer(outputSpec);
        
        for (int i = 0; i < n; i++) {
        	// check if the user cancelled the execution
            exec.checkCanceled();
            // report progress
            exec.setProgress((double)i / n, " processing row " + currentRow + " from output Table P");
        	
            DataCell[] cells = new DataCell[1]; // 1 coluna
            double prod = 1;
            for (int j = 0; j < d; j++) {
                prod = prod * p[i][j];
            }
            cells[0] = new DoubleCell(prod);
            DataRow row = new DefaultRow(new RowKey("RowKey_" + i), cells);
            containerOut.addRowToTable(row);
        }
        containerOut.close();
        BufferedDataTable out = containerOut.getTable();
            
        return new BufferedDataTable[]{out};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO Code executed on reset.
        // Models build during execute are cleared here.
        // Also data handled in load/saveInternals will be erased here.
    }

    /**
     * {@inheritDoc}
     * 
     * Test if everything is ok, if the matrix size match, if the bandwidth match with the dimensions, usw ...
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        
        // TODO: check if user settings are available, fit to the incoming
        // table structure, and the incoming types are feasible for the node
        // to execute. If the node can execute in its current state return
        // the spec of its output data table(s) (if you can, otherwise an array
        // with null elements), or throw an exception with a useful user message
        
    	
        // first of all validate the incoming data table spec
        boolean allColumnNumeric = true;
        int dimensionB = inSpecs[IN_PORT_TRAINING].getNumColumns();
        //boolean containsName = false;
        for (int i = 0; i < inSpecs[IN_PORT_TRAINING].getNumColumns(); i++) {
            DataColumnSpec columnSpec = inSpecs[IN_PORT_TRAINING].getColumnSpec(i);
            // we can only work with it, if it contains only numeric columns
            if ( ! columnSpec.getType().isCompatible(DoubleValue.class)) {
                // found one numeric column
                allColumnNumeric= false;
                break;
            }
        }
        
        if (!allColumnNumeric) {
            throw new InvalidSettingsException("Input training table must contain only numeric columns");
        }
        
        allColumnNumeric = true;
        int dimensionX = inSpecs[IN_PORT_TESTING].getNumColumns();
        for (int i = 0; i < inSpecs[IN_PORT_TESTING].getNumColumns(); i++) {
            DataColumnSpec columnSpec = inSpecs[IN_PORT_TESTING].getColumnSpec(i);
            // we can only work with it, if it contains only numeric columns
            if (! columnSpec.getType().isCompatible(DoubleValue.class)) {
                // found one numeric column
                allColumnNumeric = false;
                break;
            }    
        }
        
        if (!allColumnNumeric) {
            throw new InvalidSettingsException("Input testing table must contain only numeric columns");
        }
        
        // Checking the input bandwidth matrix
        allColumnNumeric = true;
        if ( inSpecs[IN_PORT_BANDWIDTH].getNumColumns() == 1 ) {
	        DataColumnSpec columnSpec = inSpecs[IN_PORT_BANDWIDTH].getColumnSpec(0);
	            // we can only work with it, if it contains only one numeric columns
            if (! columnSpec.getType().isCompatible(DoubleValue.class)) {
                allColumnNumeric = false;
            } 
        } else {
        	throw new InvalidSettingsException("Incompatible dimensions: The bandwidth matrix must have only one column");
        }
        
        if (!allColumnNumeric) {
            throw new InvalidSettingsException("Input table bandwidth must contain a numeric column");
        }
        
        // Check if the dimensions are equal
        if ( dimensionB != dimensionX ) {
        	throw new InvalidSettingsException("Incompatible dimensions: Number of columns of training table must be the same of testing table");
        }
        
        // so far the input is checked and the algorithm can work with the 
        // incoming data
         
        // we want to add "d" columns with the KDE(x) at d-th dimension
        DataTableSpec outputSpec = null;
        DataColumnSpec[] kdeColumns = new DataColumnSpec[1];
        
        DataColumnSpecCreator colSpecCreator = new DataColumnSpecCreator("KDE(x)", DoubleCell.TYPE);
        // if we know the number of bins we also know the number of possible
        // values of that new column
        DataColumnDomainCreator domainCreator = new DataColumnDomainCreator(new DoubleCell(0), new DoubleCell(Double.MAX_VALUE));
        // and can add this domain information to the output spec
        colSpecCreator.setDomain(domainCreator.createDomain());
        // now the column spec can be created
        kdeColumns[0] = colSpecCreator.createSpec();
     
        outputSpec = new DataTableSpec(kdeColumns);
        
        return new DataTableSpec[]{outputSpec};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {

        // TODO save user settings to the config object.
        
    	m_kernel.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // TODO load (valid) settings from the config object.
        // It can be safely assumed that the settings are valided by the 
        // method below.

    	m_kernel.loadSettingsFrom(settings);
    	//m_bandwidth.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // TODO check if the settings could be applied to our model
        // e.g. if the count is in a certain range (which is ensured by the
        // SettingsModel).
        // Do not actually set any values of any member variables.
    	
    	m_kernel.validateSettings(settings);
    	//m_bandwidth.validateSettings(settings);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        
        // TODO load internal data. 
        // Everything handed to output ports is loaded automatically (data
        // returned by the execute method, models loaded in loadModelContent,
        // and user settings set through loadSettingsFrom - is all taken care 
        // of). Load here only the other internals that need to be restored
        // (e.g. data used by the views).

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
       
        // TODO save internal models. 
        // Everything written to output ports is saved automatically (data
        // returned by the execute method, models saved in the saveModelContent,
        // and user settings saved through saveSettingsTo - is all taken care 
        // of). Save here only the other internals that need to be preserved
        // (e.g. data used by the views).

    }

}

