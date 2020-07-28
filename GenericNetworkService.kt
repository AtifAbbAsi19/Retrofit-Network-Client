interface GenericNetworkService {
    
    @Headers("Content-Type: application/json")
    @POST("/endpoint/test")
    abstract fun getEndPoint(@Header("x-bb-client-key") token: String, @Body exampleRequest: String): Observable<ExampleResponse>


}