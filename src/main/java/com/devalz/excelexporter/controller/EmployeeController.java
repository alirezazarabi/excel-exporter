package com.devalz.excelexporter.controller;

import com.devalz.excelexporter.excel.ExcelDto;
import com.devalz.excelexporter.excel.ExcelUtils;
import com.devalz.excelexporter.model.dto.EmployeeDto;
import com.devalz.excelexporter.service.api.EmployeeService;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping(value = "/employees/excel")
    public ResponseEntity<?> exportEmployeeExcel(@RequestBody EmployeeDto employeeDto,
                                                 @RequestParam int page, @RequestParam int size,
                                                 @RequestParam(required = false) String orderBy,
                                                 @RequestParam(required = false) Sort.Direction direction) {
        ExcelDto excelDto = employeeService.exportExcel(employeeDto, page, size, orderBy, direction);
        String contentDisposition = "attachment;filename=" + excelDto.getFileName();
        return ResponseEntity.ok()
                .header("Content-Type", ExcelUtils.XLSX_EXCEL_CONTENT_TYPE)
                .header("Content-Disposition", contentDisposition)
                .body(excelDto.getContent());
    }

}
