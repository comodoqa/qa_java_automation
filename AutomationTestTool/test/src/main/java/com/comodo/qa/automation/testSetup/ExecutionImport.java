package com.comodo.qa.automation.testSetup;

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;

@CsvDataType()
public class ExecutionImport {
	@CsvField(pos = 1)
    public String id;

    @CsvField(pos = 2)
    public String title;
    
    @CsvField(pos = 3)
    public String caseId;
    
    @CsvField(pos = 4)
    public String runConfiguration;
    
    @CsvField(pos = 5)
    public String runId;
    
    @CsvField(pos = 6)
    public String status;
    
}
