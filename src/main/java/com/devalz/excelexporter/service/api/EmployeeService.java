package com.devalz.excelexporter.service.api;

import com.devalz.excelexporter.utils.excel.ExcelDto;
import com.devalz.excelexporter.model.dto.EmployeeDto;
import org.springframework.data.domain.Sort;

public interface EmployeeService {

    ExcelDto exportExcel(EmployeeDto employeeDto, int page, int size, String orderBy, Sort.Direction direction);

}
