package com.example.kurikulumaplikasiinsatagram.model

class Posts {
    private var postid : String =""
    private var postImage : String =""
    private var publisher : String =""
    private var description : String =""

    constructor()
    constructor(postid: String, postImage: String, publisher: String, description: String) {
        this.postid = postid
        this.postImage = postImage
        this.publisher = publisher
        this.description = description
    }

    fun getPostId():String{
        return postid
    }
    fun getPostImage():String{
        return postImage
    }
    fun getPublisher():String{
        return publisher
    }
    fun getDescription():String{
        return description
    }

    fun setPostId(postid: String){
        this.postid = postid
    }
    fun setPostImage(postImage: String){
        this.postImage = postImage
    }
    fun setPublisher(publisher: String){
        this.publisher = publisher
    }
    fun setDescription(description: String){
        this.description = description
    }
}