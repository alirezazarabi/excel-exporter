package com.devalz.excelexporter.service.impl;

import com.devalz.excelexporter.utils.excel.ExcelDto;
import com.devalz.excelexporter.utils.excel.ExcelExporter;
import com.devalz.excelexporter.model.dto.EmployeeDto;
import com.devalz.excelexporter.model.entity.Employee;
import com.devalz.excelexporter.model.entity.Employee_;
import com.devalz.excelexporter.repository.jpa.EmployeeRepository;
import com.devalz.excelexporter.service.api.EmployeeService;
import com.devalz.excelexporter.utils.DateUtils;
import com.devalz.excelexporter.utils.PageUtils;
import com.devalz.excelexporter.utils.SpecificationUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.devalz.excelexporter.utils.excel.ExcelColumn.Type.NUMERIC;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ExcelDto exportExcel(EmployeeDto employeeDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction, true);
        Optional<Specification<Employee>> optionalSpecification = createSpecification(employeeDto);
        Page<Employee> EmployeePage = employeeRepository.findAll(optionalSpecification.get(), pageable);
        ExcelExporter<Employee> excelExporter = new ExcelExporter.Builder<Employee>()
                .fileName("employee_excel")
                .dataList(EmployeePage.getContent())
                .addColumn().headerName("title.employee.name")
                .source(Employee::getName)
                .end()
                .addColumn().headerName("title.employee.salary")
                .source(Employee::getSalary)
                .type(NUMERIC).thousandSeparatorFormat()
                .end()
                .addColumn().headerName("title.employee.employmentDate")
                .source(p -> p.getEmploymentDate() == null ? null : DateUtils.convertToDateFormat(p.getEmploymentDate().atStartOfDay()))
                .end()
                .build();
        byte[] excelContent = excelExporter.export();
        return new ExcelDto.Builder()
                .fileName(excelExporter.getFileName())
                .content(excelContent)
                .build();
    }

    private Optional<Specification<Employee>> createSpecification(EmployeeDto employeeDto) {
        List<Specification<Employee>> specificationList = new ArrayList<>();
        Specification<Employee> organEq = (root, query, criteriaBuilder) -> {
            Predicate[] predicates = new Predicate[0];
            if (StringUtils.hasText(employeeDto.getName())) {
                String pattern = "%" + employeeDto.getName() + "%";
                Predicate nameLike = criteriaBuilder.like(root.get(Employee_.NAME), pattern);
                predicates = ArrayUtils.addAll(predicates, nameLike);
            }
            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organEq);
        return SpecificationUtils.andSpecifications(specificationList);
    }


}
