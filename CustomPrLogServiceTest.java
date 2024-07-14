import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
public class CustomPRLogServiceTest {

    @Mock
    private CustomPRLogRepository customPRLogRepository;

    @Mock
    private ReportUtils reportUtils;

    @InjectMocks
    private CustomPRLogService customPRLogService;

    @BeforeEach
    void setUp() {
        customPRLogService = new CustomPRLogService();
        customPRLogService.customPRLogRepository = customPRLogRepository;
        customPRLogService.reportUtils = reportUtils;
    }

    @Test
    void testCreateNewProcessExecutionLogRecordSuccess() {
        when(customPRLogRepository.getNextProcessExecutionLogID()).thenReturn("123");
        when(reportUtils.getCurrentDateTime()).thenReturn("2024-07-14 12:34:56");

        CustomPRProcessExecutionLogObject logObject = customPRLogService.createNewProcessExecutionLogRecord(
                "procedureName", "procedureType", "procedureInitializeName",
                "procedureParams", "initiatedBy", "2024-07-14", "appName");

        assertNotNull(logObject);
        assertEquals(123, logObject.getProcedureID());
        assertEquals("procedureName", logObject.getProcedureName());
        assertEquals("procedureType", logObject.getProcedureType());
        assertEquals("procedureInitializeName", logObject.getProcedureInitializeName());
        assertEquals(Timestamp.valueOf("2024-07-14 12:34:56"), logObject.getProcedureStartTime());
        assertEquals(AppConstants.ACTIVE_FLAG_PENDING, logObject.getProcedureStatus());
        assertEquals("procedureParams", logObject.getProcedureParams());
        assertEquals("initiatedBy", logObject.getInitiatedBy());
        assertEquals(AppConstants.EMPTY_STRING, logObject.getAdditionalInfo());
        assertEquals(AppConstants.EMPTY_STRING, logObject.getLogMessage());
        assertEquals("2024-07-14", logObject.getAsOfDate());
        assertEquals("appName", logObject.getAppName());
    }

    @Test
    void testCreateNewProcessExecutionLogRecordLogIdNull() {
        when(customPRLogRepository.getNextProcessExecutionLogID()).thenReturn(null);

        CustomPRProcessExecutionLogObject logObject = customPRLogService.createNewProcessExecutionLogRecord(
                "procedureName", "procedureType", "procedureInitializeName",
                "procedureParams", "initiatedBy", "2024-07-14", "appName");

        assertNull(logObject);
    }
}
