package org.netmen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.netmen.dao.mapper.InterviewStatusMapper;
import org.netmen.dao.po.InterviewStatus;
import org.netmen.service.InterviewStatusService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InterviewStatusServiceImpl extends ServiceImpl<InterviewStatusMapper, InterviewStatus> implements InterviewStatusService {


    /**
     * 面试不通过
     *
     * @param id
     */
    @Override
    public void interviewFailed(Integer id) {
        InterviewStatus interviewStatus = getById(id);
        Boolean adjust = interviewStatus.getAdjust();
        // 1.如果当前处于第一志愿部门面试
        if (interviewStatus.getCurDepartmentId().equals(interviewStatus.getFirstDepartmentId())) {
            // 第一志愿部门和第二志愿部门相同时
            if (interviewStatus.getFirstDepartmentId().equals(interviewStatus.getSecondDepartmentId())) {
                // 是否调剂意愿为否
                if (adjust.equals(false)) {
                    // 淘汰
                    lambdaUpdate()
                            .set(InterviewStatus::getStatus, ((short) 2))
                            .eq(InterviewStatus::getId, id)
                            .eq(InterviewStatus::getStatus, interviewStatus.getStatus())
                            .update();
                    return;
                } else {
                    lambdaUpdate()
                            .set(InterviewStatus::getCurDepartmentId, 0)
                            .eq(InterviewStatus::getId, id)
                            .eq(InterviewStatus::getCurDepartmentId, interviewStatus.getCurDepartmentId())
                            .update();
                    return;
                }
            } else {
                // 不相同则进入第二志愿面试状态
                lambdaUpdate()
                        .set(InterviewStatus::getCurDepartmentId, interviewStatus.getSecondDepartmentId())
                        .eq(InterviewStatus::getId, id)
                        .eq(InterviewStatus::getCurDepartmentId, interviewStatus.getCurDepartmentId())
                        .update();
                return;
            }
        }
        // 2.当前处于第二志愿部门面试
        if (interviewStatus.getCurDepartmentId().equals(interviewStatus.getSecondDepartmentId())) {
            // 是否调剂意愿为否
            if (adjust.equals(false)) {
                // 淘汰
                lambdaUpdate()
                        .set(InterviewStatus::getStatus, ((short) 2))
                        .eq(InterviewStatus::getId, id)
                        .eq(InterviewStatus::getStatus, interviewStatus.getStatus())
                        .update();
                return;
            } else {
                // 进入待调剂状态
                lambdaUpdate()
                        .set(InterviewStatus::getCurDepartmentId, 0)
                        .eq(InterviewStatus::getId, id)
                        .eq(InterviewStatus::getCurDepartmentId, interviewStatus.getCurDepartmentId())
                        .update();
                return;
            }

        }

        // 3.当前处于调剂部门面试,被淘汰后继续等待调剂
        if (interviewStatus.getCurDepartmentId() != 0) {
            lambdaUpdate()
                    .set(InterviewStatus::getCurDepartmentId, 0)
                    .eq(InterviewStatus::getId, id)
                    .eq(InterviewStatus::getCurDepartmentId, interviewStatus.getCurDepartmentId())
                    .update();
            return;
        }

        // 没有部门挑选，彻底淘汰
        lambdaUpdate()
                .set(InterviewStatus::getStatus, ((short) 2))
                .eq(InterviewStatus::getId, id)
                .eq(InterviewStatus::getStatus, interviewStatus.getStatus())
                .update();
    }

    /**
     * 面试通过
     */
    @Override
    public void interviewPass(Integer id) {
        InterviewStatus interviewStatus = getById(id);
        lambdaUpdate()
                .set(InterviewStatus::getStatus, ((short) 1))
                .eq(InterviewStatus::getId, id)
                .eq(InterviewStatus::getStatus, interviewStatus.getStatus())
                .update();
    }

    /**
     * 调剂部门挑选接口
     */
    @Override
    public void adjustDepartmentSelect(Integer departmentId, Integer id) {
        InterviewStatus interviewStatus = getById(id);
        lambdaUpdate()
                .set(InterviewStatus::getCurDepartmentId, departmentId)
                .eq(InterviewStatus::getId, id)
                .eq(InterviewStatus::getCurDepartmentId, interviewStatus.getCurDepartmentId())
                .update();
    }

    /**
     * 防误触接口
     *
     * @param id
     */
    @Override
    public void falseTouchRejection(Integer id) {
        InterviewStatus interviewStatus = getById(id);
        // 如果当前为待面试状态
        if (interviewStatus.getStatus().equals((short) 0)) {
            // 1.如果当前处于第二志愿部门面试
            if (interviewStatus.getCurDepartmentId().equals(interviewStatus.getSecondDepartmentId())) {
                // 回退到第一志愿部门
                lambdaUpdate()
                        .set(InterviewStatus::getCurDepartmentId, interviewStatus.getFirstDepartmentId())
                        .eq(InterviewStatus::getId, id)
                        .eq(InterviewStatus::getCurDepartmentId, interviewStatus.getCurDepartmentId())
                        .update();
            } else {
                // 回退到第二志愿部门
                lambdaUpdate()
                        .set(InterviewStatus::getCurDepartmentId, interviewStatus.getSecondDepartmentId())
                        .eq(InterviewStatus::getId, id)
                        .eq(InterviewStatus::getCurDepartmentId, interviewStatus.getCurDepartmentId())
                        .update();
            }
        } else if (interviewStatus.getStatus().equals(((short) 1))) {
            // 当前为录取状态,则回退为待面试状态
            lambdaUpdate()
                    .set(InterviewStatus::getStatus, ((short) 0))
                    .eq(InterviewStatus::getId, id)
                    .eq(InterviewStatus::getStatus, interviewStatus.getStatus())
                    .update();
        } else {
            // 当前为淘汰状态,则回退为待面试状态
            lambdaUpdate()
                    .set(InterviewStatus::getStatus, ((short) 0))
                    .eq(InterviewStatus::getId, id)
                    .eq(InterviewStatus::getStatus, interviewStatus.getStatus())
                    .update();
        }
    }

    /**
     * 初始化面试状态
     *
     * @param id
     */
    @Override
    public void initialize(Integer id) {
        InterviewStatus interviewStatus = getById(id);
        lambdaUpdate()
                .set(InterviewStatus::getStatus, ((short) 0))
                .set(InterviewStatus::getCurDepartmentId, interviewStatus.getFirstDepartmentId())
                .eq(InterviewStatus::getId, id)
                .eq(InterviewStatus::getStatus, interviewStatus.getStatus())
                .update();
    }


}
