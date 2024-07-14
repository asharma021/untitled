import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
public class CustomPRQScoreMonitoringServiceTest {

    @Mock
    private CustomPRQScoreMonitoringRepository customPRQScoreMonitoringRepository;

    @Mock
    private CustomPRAgGridService customPRAgGridService;

    @Mock
    private ReportUtils reportUtils;

    @Mock
    private CustomPRcsvUtility customPRcsvUtility;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private CustomPRQScoreMonitoringService customPRQScoreMonitoringService;

    private static final Logger logger = LoggerFactory.getLogger(CustomPRQScoreMonitoringService.class);

    private CustomPRQScoreMonitoringSearchObject searchObject;

    @BeforeEach
    void setUp() {
        searchObject = new CustomPRQScoreMonitoringSearchObject();
        searchObject.setScreenName("testScreen");
        searchObject.setTabName("testTab");
        searchObject.setFrequency("Daily");
    }

    @Test
    void testGetQScoreMonitoringDataReturnsError() {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put(AppConstants.ERROR, "error");

        when(customPRQScoreMonitoringRepository.getQScoreMonitoringViewNameAndFilterCondition(anyString(), anyString(), anyString()))
                .thenReturn(errorMap);

        CustomPRQScoreMonitoringResponseObject responseObject = customPRQScoreMonitoringService.getQScoreMonitoringData(searchObject);

        assertNotNull(responseObject);
        assertEquals(1, responseObject.getData().size());
        assertEquals(errorMap, responseObject.getData().get(0));
        assertEquals(1, responseObject.getLastRow());
    }

    @Test
    void testGetQScoreMonitoringDataReturnsData() {
        Map<String, Object> viewNameFilterConditionMap = new HashMap<>();
        viewNameFilterConditionMap.put(AppConstants.VIEW_NAME, "testView");

        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put(AppConstants.COLUMN_SELECT, "col1, col2");
        filterMap.put(AppConstants.WHERE_CLAUSE, "1=1");

        List<Map<String, Object>> mockData = List.of(Map.of("col1", "value1", "col2", "value2"));

        when(customPRQScoreMonitoringRepository.getQScoreMonitoringViewNameAndFilterCondition(anyString(), anyString(), anyString()))
                .thenReturn(viewNameFilterConditionMap);
        when(customPRAgGridService.getFilterMap(any(), any())).thenReturn(filterMap);
        when(customPRQScoreMonitoringRepository.getQScoreMonitoringData(anyString())).thenReturn(mockData);
        when(reportUtils.formatDates(anyList())).thenReturn(mockData);

        CustomPRQScoreMonitoringResponseObject responseObject = customPRQScoreMonitoringService.getQScoreMonitoringData(searchObject);

        assertNotNull(responseObject);
        assertEquals(mockData, responseObject.getData());
        assertEquals(mockData.size(), responseObject.getLastRow());
    }

    @Test
    void testGetQScoreMonitoringDataWithPivot() {
        searchObject.setPivot(true);

        Map<String, Object> viewNameFilterConditionMap = new HashMap<>();
        viewNameFilterConditionMap.put(AppConstants.VIEW_NAME, "testView");

        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put(AppConstants.COLUMN_SELECT, "col1, col2");
        filterMap.put(AppConstants.WHERE_CLAUSE, "1=1");
        filterMap.put(AppConstants.PIVOT_COLUMNS, "pivotCol");

        List<Map<String, Object>> mockData = List.of(Map.of("col1", "value1", "col2", "value2"));

        when(customPRQScoreMonitoringRepository.getQScoreMonitoringViewNameAndFilterCondition(anyString(), anyString(), anyString()))
                .thenReturn(viewNameFilterConditionMap);
        when(customPRAgGridService.getFilterMap(any(), any())).thenReturn(filterMap);
        when(customPRAgGridService.generatePivotColumns(anyList())).thenReturn("pivotCol");
        when(customPRQScoreMonitoringRepository.getQScoreMonitoringData(anyString())).thenReturn(mockData);
        when(reportUtils.formatDates(anyList())).thenReturn(mockData);

        CustomPRQScoreMonitoringResponseObject responseObject = customPRQScoreMonitoringService.getQScoreMonitoringData(searchObject);

        assertNotNull(responseObject);
        assertEquals(mockData, responseObject.getData());
        assertEquals(mockData.size(), responseObject.getLastRow());
    }

    @Test
    void testGetQScoreMonitoringDataThrowsException() {
        Map<String, Object> viewNameFilterConditionMap = new HashMap<>();
        viewNameFilterConditionMap.put(AppConstants.VIEW_NAME, "testView");

        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put(AppConstants.COLUMN_SELECT, "col1, col2");
        filterMap.put(AppConstants.WHERE_CLAUSE, "1=1");

        when(customPRQScoreMonitoringRepository.getQScoreMonitoringViewNameAndFilterCondition(anyString(), anyString(), anyString()))
                .thenReturn(viewNameFilterConditionMap);
        when(customPRAgGridService.getFilterMap(any(), any())).thenReturn(filterMap);
        when(customPRQScoreMonitoringRepository.getQScoreMonitoringData(anyString())).thenThrow(new RuntimeException("Test exception"));

        CustomPRQScoreMonitoringResponseObject responseObject = customPRQScoreMonitoringService.getQScoreMonitoringData(searchObject);

        assertNotNull(responseObject);
        assertTrue(responseObject.getData().get(0).containsKey(AppConstants.ERROR));
        assertEquals("Test exception", responseObject.getData().get(0).get(AppConstants.ERROR));
    }
}
