// 全局变量
let myChart = null;
let currentPage = 1;
const pageSize = 10;

// API基础URL
const API_BASE_URL = '/api/shopping-records';

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
    // 初始化图表
    myChart = echarts.init(document.getElementById('chart-container'));
    
    // 加载初始数据
    loadGenderStats();
    loadShoppingRecords();
    loadCategories();
    
    // 绑定事件监听器
    bindEventListeners();
});

// 绑定事件监听器
function bindEventListeners() {
    // 统计类型切换
    document.querySelectorAll('.list-group-item').forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            // 更新active状态
            document.querySelectorAll('.list-group-item').forEach(i => i.classList.remove('active'));
            item.classList.add('active');
            
            // 加载对应的统计数据
            const type = item.getAttribute('data-type');
            loadStats(type);
        });
    });

    // 搜索按钮点击事件
    document.getElementById('search-btn').addEventListener('click', () => {
        currentPage = 1;
        loadShoppingRecords();
    });

    // 文件上传表单提交
    document.getElementById('upload-form').addEventListener('submit', handleFileUpload);
}

// 加载统计数据
function loadStats(type) {
    const endpoints = {
        gender: '/stats/gender',
        age: '/stats/age-group',
        category: '/stats/category',
        season: '/stats/season',
        location: '/stats/location',
        date: '/stats/date-range',
        customer: '/stats/customer'
    };

    const titles = {
        gender: '按性别统计购物金额',
        age: '按年龄段统计购物金额',
        category: '按商品类别统计购物金额',
        season: '按季节统计购物金额',
        location: '按地区统计购物金额',
        date: '按日期统计购物金额',
        customer: '客户购买统计'
    };

    // 更新图表标题
    document.getElementById('chart-title').textContent = titles[type];

    // 获取数据并绘制图表
    axios.get(API_BASE_URL + endpoints[type])
        .then(response => {
            const data = response.data.data;
            drawChart(type, data);
        })
        .catch(error => {
            console.error('加载统计数据失败:', error);
            showError('加载统计数据失败，请稍后重试');
        });
}

// 绘制图表
function drawChart(type, data) {
    let option = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        }
    };

    switch (type) {
        case 'gender':
            option = createGenderChartOption(data);
            break;
        case 'age':
            option = createAgeChartOption(data);
            break;
        case 'category':
            option = createCategoryChartOption(data);
            break;
        case 'season':
            option = createSeasonChartOption(data);
            break;
        case 'location':
            option = createLocationChartOption(data);
            break;
        case 'date':
            option = createDateChartOption(data);
            break;
        case 'customer':
            option = createCustomerChartOption(data);
            break;
    }

    myChart.setOption(option);
}

// 创建性别统计图表配置
function createGenderChartOption(data) {
    return {
        title: {
            text: '购物金额性别分布'
        },
        tooltip: {
            trigger: 'item',
            formatter: '{b}: {c} ({d}%)'
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            data: data.map(item => item.gender)
        },
        series: [{
            type: 'pie',
            radius: '50%',
            data: data.map(item => ({
                name: item.gender,
                value: item.total_amount
            })),
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }]
    };
}

// 创建年龄段统计图表配置
function createAgeChartOption(data) {
    return {
        title: {
            text: '不同年龄段购物金额分布'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['购物金额', '购买次数']
        },
        xAxis: {
            type: 'category',
            data: data.map(item => item.age_group)
        },
        yAxis: [
            {
                type: 'value',
                name: '金额',
                axisLabel: {
                    formatter: '{value}'
                }
            },
            {
                type: 'value',
                name: '次数',
                axisLabel: {
                    formatter: '{value}'
                }
            }
        ],
        series: [
            {
                name: '购物金额',
                type: 'bar',
                data: data.map(item => item.total_amount)
            },
            {
                name: '购买次数',
                type: 'line',
                yAxisIndex: 1,
                data: data.map(item => item.purchase_count)
            }
        ]
    };
}

// 创建商品类别统计图表配置
function createCategoryChartOption(data) {
    return {
        title: {
            text: '不同商品类别购物金额分布'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['购物金额']
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'value',
            boundaryGap: [0, 0.01]
        },
        yAxis: {
            type: 'category',
            data: data.map(item => item.category)
        },
        series: [
            {
                name: '购物金额',
                type: 'bar',
                data: data.map(item => item.total_amount)
            }
        ]
    };
}

// 创建季节统计图表配置
function createSeasonChartOption(data) {
    return {
        title: {
            text: '不同季节购物金额分布'
        },
        tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            data: data.map(item => item.season)
        },
        series: [
            {
                name: '购物金额',
                type: 'pie',
                radius: '55%',
                center: ['50%', '60%'],
                data: data.map(item => ({
                    name: item.season,
                    value: item.total_amount
                })),
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };
}

// 创建地区统计图表配置
function createLocationChartOption(data) {
    // 按金额排序
    data.sort((a, b) => b.total_amount - a.total_amount);
    
    // 只取前10个地区
    const topLocations = data.slice(0, 10);
    
    return {
        title: {
            text: '不同地区购物金额分布 (前10名)'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: topLocations.map(item => item.location),
            axisLabel: {
                interval: 0,
                rotate: 30
            }
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                name: '购物金额',
                type: 'bar',
                data: topLocations.map(item => item.total_amount)
            }
        ]
    };
}

// 创建日期统计图表配置
function createDateChartOption(data) {
    return {
        title: {
            text: '购物金额时间趋势'
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: ['购物金额', '购买次数']
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: data.map(item => item.date)
        },
        yAxis: [
            {
                type: 'value',
                name: '金额'
            },
            {
                type: 'value',
                name: '次数'
            }
        ],
        series: [
            {
                name: '购物金额',
                type: 'line',
                data: data.map(item => item.total_amount)
            },
            {
                name: '购买次数',
                type: 'line',
                yAxisIndex: 1,
                data: data.map(item => item.purchase_count)
            }
        ]
    };
}

// 创建客户购买统计图表配置
function createCustomerChartOption(data) {
    // 只取前20个客户
    const topCustomers = data.slice(0, 20);
    
    return {
        title: {
            text: '客户购买统计 (前20名)'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['购物总额', '平均金额']
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: [
            {
                type: 'category',
                data: topCustomers.map(item => 'Customer ' + item.customer_id),
                axisLabel: {
                    interval: 0,
                    rotate: 45
                }
            }
        ],
        yAxis: [
            {
                type: 'value',
                name: '总额',
                min: 0
            },
            {
                type: 'value',
                name: '平均',
                min: 0
            }
        ],
        series: [
            {
                name: '购物总额',
                type: 'bar',
                data: topCustomers.map(item => item.total_amount)
            },
            {
                name: '平均金额',
                type: 'line',
                yAxisIndex: 1,
                data: topCustomers.map(item => item.avg_amount)
            }
        ]
    };
}

// 加载购物记录
function loadShoppingRecords() {
    const category = document.getElementById('category-filter').value;
    const gender = document.getElementById('gender-filter').value;
    const season = document.getElementById('season-filter').value;

    axios.get(`${API_BASE_URL}/page`, {
        params: {
            current: currentPage,
            size: pageSize,
            category: category || null,
            gender: gender || null,
            season: season || null
        }
    })
    .then(response => {
        const { list, total, pageNum } = response.data.data;
        updateTable(list);
        updatePagination(total, pageNum);
    })
    .catch(error => {
        console.error('加载购物记录失败:', error);
        showError('加载购物记录失败，请稍后重试');
    });
}

// 更新表格内容
function updateTable(records) {
    const tbody = document.getElementById('data-table-body');
    tbody.innerHTML = '';

    records.forEach(record => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${record.id}</td>
            <td>${record.date}</td>
            <td>${record.customerId}</td>
            <td>${record.age}</td>
            <td>${record.gender}</td>
            <td>${record.item}</td>
            <td>${record.category}</td>
            <td>${record.purchaseAmount}</td>
            <td>${record.location}</td>
            <td>${record.size || '-'}</td>
            <td>${record.color || '-'}</td>
            <td>${record.season}</td>
            <td>${record.rating}</td>
            <td>${record.subscriptionStatus || '-'}</td>
            <td>${record.discountApplied || '-'}</td>
            <td>${record.promoCodeUsed || '-'}</td>
        `;
        tbody.appendChild(tr);
    });
}

// 更新分页
function updatePagination(total, current) {
    const pagination = document.getElementById('pagination');
    const totalPages = Math.ceil(total / pageSize);
    
    let html = '';
    
    // 上一页
    html += `
        <li class="page-item ${current === 1 ? 'disabled' : ''}">
            <a class="page-link" href="#" data-page="${current - 1}">上一页</a>
        </li>
    `;

    // 页码
    for (let i = 1; i <= totalPages; i++) {
        if (i === 1 || i === totalPages || (i >= current - 2 && i <= current + 2)) {
            html += `
                <li class="page-item ${i === current ? 'active' : ''}">
                    <a class="page-link" href="#" data-page="${i}">${i}</a>
                </li>
            `;
        } else if (i === current - 3 || i === current + 3) {
            html += `
                <li class="page-item disabled">
                    <a class="page-link" href="#">...</a>
                </li>
            `;
        }
    }

    // 下一页
    html += `
        <li class="page-item ${current === totalPages ? 'disabled' : ''}">
            <a class="page-link" href="#" data-page="${current + 1}">下一页</a>
        </li>
    `;

    pagination.innerHTML = html;

    // 绑定分页点击事件
    pagination.querySelectorAll('.page-link').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const page = parseInt(e.target.getAttribute('data-page'));
            if (page && page !== current && page > 0 && page <= totalPages) {
                currentPage = page;
                loadShoppingRecords();
            }
        });
    });
}

// 加载商品类别
function loadCategories() {
    axios.get(`${API_BASE_URL}/stats/category`)
        .then(response => {
            const categories = response.data.data;
            const select = document.getElementById('category-filter');
            
            categories.forEach(category => {
                const option = document.createElement('option');
                option.value = category.category;
                option.textContent = category.category;
                select.appendChild(option);
            });
        })
        .catch(error => {
            console.error('加载商品类别失败:', error);
        });
}

// 处理文件上传
function handleFileUpload(e) {
    e.preventDefault();
    
    const fileInput = document.getElementById('csv-file');
    const file = fileInput.files[0];
    
    if (!file) {
        showError('请选择要上传的CSV文件');
        return;
    }

    const formData = new FormData();
    formData.append('file', file);

    // 显示进度条
    const progress = document.querySelector('.progress');
    const progressBar = progress.querySelector('.progress-bar');
    progress.style.display = 'block';
    progressBar.style.width = '0%';

    axios.post('/api/csv/import', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        onUploadProgress: (progressEvent) => {
            const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
            progressBar.style.width = percentCompleted + '%';
            progressBar.textContent = percentCompleted + '%';
        }
    })
    .then(response => {
        showSuccess('数据导入成功');
        // 重新加载数据
        loadShoppingRecords();
        loadStats(document.querySelector('.list-group-item.active').getAttribute('data-type'));
    })
    .catch(error => {
        console.error('数据导入失败:', error);
        showError('数据导入失败，请检查文件格式是否正确');
    })
    .finally(() => {
        // 隐藏进度条
        setTimeout(() => {
            progress.style.display = 'none';
            progressBar.style.width = '0%';
        }, 1000);
    });
}

// 显示错误信息
function showError(message) {
    const resultDiv = document.getElementById('import-result');
    resultDiv.className = 'alert alert-danger';
    resultDiv.textContent = message;
    setTimeout(() => {
        resultDiv.className = '';
        resultDiv.textContent = '';
    }, 3000);
}

// 显示成功信息
function showSuccess(message) {
    const resultDiv = document.getElementById('import-result');
    resultDiv.className = 'alert alert-success';
    resultDiv.textContent = message;
    setTimeout(() => {
        resultDiv.className = '';
        resultDiv.textContent = '';
    }, 3000);
}

// 窗口大小改变时重绘图表
window.addEventListener('resize', () => {
    if (myChart) {
        myChart.resize();
    }
});