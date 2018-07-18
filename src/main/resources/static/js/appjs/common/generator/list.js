var prefix = "/common/generator";
$(function() {
    load();
});

function load() {
    $('#exampleTable')
        .bootstrapTable(
            {
                method : 'get', // 服务器数据的请求方式 get or post
                url : prefix + "/list", // 服务器数据的加载地址
                cache : false,// 设置为 false 禁用 AJAX 数据缓存， 默认为true
                showRefresh : true,//设置刷新按钮
                showToggle : true,
                showColumns : true,//设置显示字段选择按钮
                iconSize : 'outline',
                toolbar : '#exampleToolbar',
                striped : true, // 设置为true会有隔行变色效果
                dataType : "json", // 服务器返回的数据类型
                pagination : true, // 设置为true会在底部显示分页条
                // queryParamsType : "limit",
                // //设置为limit则会发送符合RESTFull格式的参数
                singleSelect : false, // 设置为true将禁止多选
                // contentType : "application/x-www-form-urlencoded",
                // //发送到服务器的数据编码类型
                pageSize : 10, // 如果设置了分页，每页数据条数
                pageList: [10,25,50,100],//设置页面可以显示的数据条数
                pageNumber : 1, // 如果设置了分布，首页页码
                search : true, // 是否显示搜索框
                showColumns : true, // 是否显示内容下拉框（选择显示的列）
                sidePagination : "client", // 设置在哪里进行分页，可选值为"client" 或者
                // "server"
                // queryParams : queryParams,
                // //请求服务器数据时，你可以通过重写参数的方式添加一些额外的参数，例如 toolbar 中的参数 如果
                // queryParamsType = 'limit' ,返回参数必须包含
                // limit, offset, search, sort, order 否则, 需要包含:
                // pageSize, pageNumber, searchText, sortName,
                // sortOrder.
                // 返回false将会终止请求
                columns : [
                    {
                        checkbox : true,
                        align:'center'// 居中显示
                    },
                    {
                        field : 'tableName', // 列字段名
                        title : '表名称', // 列标题
                        align : 'center',// 左右居中
                        valign : 'middle'// 上下居中
                    },
                    {
                        field : 'engine',
                        title : 'engine',
                        align : 'center',
                        valign : 'middle'
                    },
                    {
                        field : 'tableComment',
                        title : '表描述',
                        align : 'center',
                        valign : 'middle'
                    },
                    {
                        field : 'createTime',
                        title : '创建时间',
                        align : 'center',
                        valign : 'middle'
                    },
                    {
                        title : '操作',
                        field : 'id',
                        align : 'center',
                        formatter : function(value, row, index) {
                            var e = '<a class="btn btn-primary btn-sm" href="#" mce_href="#" title="生成代码" onclick="code(\''
                                + row.tableName
                                + '\')"><i class="fa fa-code"></i></a> ';
                            return e;
                        }
                    } ]
            });
}
function reLoad() {
    $('#exampleTable').bootstrapTable('refresh');
}
function code(tableName) {
    location.href = prefix + "/code/" + tableName;
}
function batchCode() {
    var rows = $('#exampleTable').bootstrapTable('getSelections'); // 返回所有选择的行，当没有选择的记录时，返回一个空数组
    if (rows.length == 0) {
        layer.msg("请选择要生成代码的表");
        return;
    }
    var tables = new Array();
    // 遍历所有选择的行数据，取每条数据对应的ID
    $.each(rows, function(i, row) {
        tables[i] = row['tableName'];
    });
    // location.href = prefix + "/batchCode?tables=" + JSON.stringify(tables);
    location.href = prefix + "/batchCode?tables=" + tables;
}

function edit(){
    console.log('打开配置页面');
    layer.open({
        type : 2,
        title : '查看配置文件',
        maxmin : true,
        shadeClose : false,
        area : [ '800px', '520px' ],
        content : prefix + '/edit',
    });
}