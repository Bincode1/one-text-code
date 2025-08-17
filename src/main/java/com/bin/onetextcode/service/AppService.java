package com.bin.onetextcode.service;

import com.bin.onetextcode.common.DeleteRequest;
import com.bin.onetextcode.model.dto.app.AppAddRequest;
import com.bin.onetextcode.model.dto.app.AppQueryRequest;
import com.bin.onetextcode.model.dto.app.AppUpdateRequest;
import com.bin.onetextcode.model.entity.App;
import com.bin.onetextcode.model.entity.User;
import com.bin.onetextcode.model.vo.AppVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author 26961
 * @since 2025-08-16
 */
public interface AppService extends IService<App> {

    Long addApp(AppAddRequest appAddRequest, HttpServletRequest request);

    Boolean deleteApp(DeleteRequest deleteRequest, HttpServletRequest request);

    Boolean updateApp(AppUpdateRequest updateRequest, HttpServletRequest request);

    AppVO getAppVOById(long id, HttpServletRequest request);

    List<AppVO> listMyAppVOByPage(AppQueryRequest queryRequest, HttpServletRequest request);


    AppVO getAppVO(App app);

    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    List<AppVO> getAppVOList(List<App> appList);

    Flux<ServerSentEvent<String>> chatToGenCode(Long appId, String userMessage, User loginUser);

    String deployApp(Long appId, User loginUser);
}
