# Retrofit-Network-Client
Retrofit using Builder Pattren with Rx2 Android


     val retrofitNetwork = RetrofitNetworkClient.Builder().baseURL(BuildConfig.END_POINT).build()

      retrofitNetwork.genericNetworkService
                    .getEndPoint(exampleRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<ExampleResponse> {
                        override fun onComplete() {
                           //after completing call
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(response: ExampleResponse) {
                           
                           Log.d(" testing : ", "Response" + response)
                        }

                        override fun onError(ex: Throwable) {
                           
                           showToast(ex.message)
                         
                           Log.d(" testing : ", "Response" + ex?.message)
                           
                        }

                    })
