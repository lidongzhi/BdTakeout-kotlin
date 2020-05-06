package com.heima.takeout.model.beans

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.io.Serializable

@DatabaseTable(tableName = "t_address") class RecepitAddressBean() : Serializable {
    @DatabaseField(generatedId = true)
    var id: Int = 0
    @DatabaseField(columnName = "username")
    var username: String = ""
    @DatabaseField(columnName = "sex")
    var sex: String = "女"
    @DatabaseField(columnName = "phone")
    var phone: String = ""
    @DatabaseField(columnName = "phoneOther")
    var phoneOther: String = ""
    @DatabaseField(columnName = "address")
    var address: String = ""
    @DatabaseField(columnName = "detailAddress")
    var detailAddress: String = ""
    @DatabaseField(columnName = "label")
    var label = ""
    @DatabaseField(columnName = "userId")
    var userId: String = "38"

    constructor(id: Int, username: String, sex: String, phone: String, phoneOther: String,
                address: String, detailAddress: String, label: String, userId: String) : this() {
        this.id = id
        this.username = username
        this.sex = sex
        this.phone = phone
        this.phoneOther = phoneOther
        this.address = address
        this.detailAddress = detailAddress
        this.label = label
        this.userId = userId
    }
}