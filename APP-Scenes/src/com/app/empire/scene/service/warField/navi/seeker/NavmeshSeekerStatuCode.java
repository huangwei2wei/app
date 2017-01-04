package com.app.empire.scene.service.warField.navi.seeker;


public enum NavmeshSeekerStatuCode {
	Success,    //寻路成功
    Failed,    //寻路失败
    NoMeshData,    //没有数据
    NoStartTriOrEndTri,    //没有起始点或终点
    NavIDNotMatch, //导航网格的索引和id不匹配
    NotFoundPath,  //找不到路径
    CanNotGetNextWayPoint,//找不到下一个拐点信息
    NoCrossPoint,
    FixPointFailed, //修复点失败
}
