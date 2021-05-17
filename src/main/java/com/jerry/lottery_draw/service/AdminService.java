package com.jerry.lottery_draw.service;

import cn.hutool.core.bean.BeanUtil;
import com.jerry.lottery_draw.domain.TAdmin;
import com.jerry.lottery_draw.domain.TAdminExample;
import com.jerry.lottery_draw.exception.BusinessException;
import com.jerry.lottery_draw.exception.BusinessExceptionCode;
import com.jerry.lottery_draw.mapper.TAdminMapper;
import com.jerry.lottery_draw.req.AdminLoginReq;
import com.jerry.lottery_draw.resp.AdminLoginResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AdminService {

    private static final Logger LOG = LoggerFactory.getLogger(AdminService.class);

    @Resource
    private TAdminMapper tAdminMapper;

    public TAdmin selectByAccount(String account) {
        TAdminExample tAdminExample = new TAdminExample();
        tAdminExample.createCriteria().andAccountEqualTo(account);
        List<TAdmin> tAdminList = tAdminMapper.selectByExample(tAdminExample);
        if (CollectionUtils.isEmpty(tAdminList)) {
            return null;
        }
        else {
            return tAdminList.get(0);
        }
    }

    public AdminLoginResp login(AdminLoginReq req) {
        TAdmin adminDb = selectByAccount(req.getAccount());
        if (ObjectUtils.isEmpty(adminDb)) {
            // 用户名不存在
            LOG.info("用户名不存在, {}", req.getAccount());
            throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
        }
        else {
            if (adminDb.getPassword().equals(req.getPassword())) {
                // 登录成功
                AdminLoginResp userLoginResp = new AdminLoginResp();
                BeanUtil.copyProperties(adminDb, userLoginResp);
                return userLoginResp;
            }
            else {
                // 密码不对
                LOG.info("密码不对, 输入密码：{}, 数据库密码：{}", req.getPassword(), adminDb.getPassword());
                throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
            }
        }
    }
}

