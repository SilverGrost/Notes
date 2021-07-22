package ru.geekbrains.notes

class Settings {
    var orderType = 0
    var textSizeId = 0
    var maxCountLinesId = 0
    var isCloudSync = false
    var authTypeService = 0
    var textSize = 0f
    var maxCountLines = 0
    var currentPosition = 0
    var userNameVK: String? = null

    constructor() {}
    constructor(orderType: Int, textSizeId: Int, maxCountLinesId: Int, authTypeService: Int) {
        this.orderType = orderType
        this.textSizeId = textSizeId
        this.maxCountLinesId = maxCountLinesId
        this.authTypeService = authTypeService
    }

    constructor(orderType: Int, textSizeId: Int, maxCountLinesId: Int) {
        this.orderType = orderType
        this.textSizeId = textSizeId
        this.maxCountLinesId = maxCountLinesId
    }
}