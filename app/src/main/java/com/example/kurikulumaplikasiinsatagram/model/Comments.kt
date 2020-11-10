package com.example.kurikulumaplikasiinsatagram.model

class Comments {
    private var Comments = ""
    private var publisher = ""

    constructor()
    constructor(Comments: String, publisher: String) {
        this.Comments = Comments
        this.publisher = publisher
    }

    fun getComment(): String{
        return Comments
    }

    fun setComment(comments: String){
        this.Comments = comments
    }

    fun getPublisher():String{
        return publisher
    }

    fun setPublisher(publisher: String){
        this.publisher = publisher
    }
}