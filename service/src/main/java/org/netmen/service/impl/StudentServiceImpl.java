package org.netmen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.netmen.dao.mapper.InterviewStatusMapper;
import org.netmen.dao.mapper.StudentMapper;
import org.netmen.dao.po.InterviewStatus;
import org.netmen.dao.po.Student;
import org.netmen.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {


    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private InterviewStatusMapper interviewStatusMapper;


    /**
     * 模糊查询
     *
     * @param name
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<Student> findStudentsByName(String name, Integer pageNo, Integer pageSize) {

        // 创建分页对象
        Page<Student> page = new Page<Student>(pageNo, pageSize);
        QueryWrapper<Student> queryWrapper = new QueryWrapper<Student>().like("name", name);
        return studentMapper.selectPage(page, queryWrapper).getRecords();
    }

    /**
     * 新增学生，并添加志愿信息
     *
     * @param student
     * @param interviewStatus
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveStudent(Student student, InterviewStatus interviewStatus) {
        // 1. 添加student
        studentMapper.insert(student);
        interviewStatus.setId(student.getId());
        interviewStatus.setCurDepartmentId(interviewStatus.getFirstDepartmentId());
        // 2. 添加interviewStatus
        interviewStatusMapper.insert(interviewStatus);
    }

    /**
     * 物理删除
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStudentByIds(List<Integer> ids) {
        studentMapper.deleteBatchIds(ids);
        interviewStatusMapper.deleteBatchIds(ids);
    }



    /**
     * 根据学号查询
     * @param studentId
     * @return
     */
    @Override
    public Student getByStudentId(String studentId) {
        return studentMapper.getByStudentId(studentId);

    }
}
