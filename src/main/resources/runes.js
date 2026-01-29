option = {
    title: {
        text: 'Stacked Line'
    },
    tooltip: {
        trigger: 'axis'
    },
    legend: {
        "data": [
            "Rune 1",
            "Rune 2",
            "Rune 3",
            "Rune 4",
            "Rune 5"
        ]
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    toolbox: {
        feature: {
            saveAsImage: {}
        }
    },
    xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun', 'Demo']
    },
    yAxis: {
        type: 'value'
    },
    series: [
        {
            name: 'Rune 1',
            type: 'line',
            data: [120, 132, 101, 134, 90, 230, 210, 2000]
        },
        {
            name: 'Union Ads',
            type: 'line',
            data: [220, 182, 191, 234, 290, 330, 310]
        },
        {
            name: 'Video Ads',
            type: 'line',
            data: [150, 232, 201, 154, 190, 330, 410]
        },
        {
            name: 'Direct',
            type: 'line',
            data: [320, 332, 301, 334, 390, 330, 320]
        },
        {
            name: 'Search Engine',
            type: 'line',
            data: [820, 932, 901, 934, 1290, 1330, 1320]
        }
    ]
};
