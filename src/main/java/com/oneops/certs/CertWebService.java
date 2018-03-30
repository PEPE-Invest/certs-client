package com.oneops.certs;


import com.oneops.certs.model.CreateReq;
import com.oneops.certs.model.DownloadReq;
import com.oneops.certs.model.DownloadRes;
import com.oneops.certs.model.ExistsReq;
import com.oneops.certs.model.ExistsRes;
import com.oneops.certs.model.ExpirationReq;
import com.oneops.certs.model.ExpirationRes;
import com.oneops.certs.model.ObsoleteReq;
import com.oneops.certs.model.RenewReq;
import com.oneops.certs.model.Response;
import com.oneops.certs.model.Result;
import com.oneops.certs.model.RevokeReq;
import com.oneops.certs.model.RevokeRes;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CertWebService {

  @POST("/certificate/create")
  Call<Result<Response>> create(@Body CreateReq req);

  @POST("/certificate/v2/renew")
  Call<Result<Response>> renew(@Body RenewReq req);

  @POST("/certificate/obsolete")
  Call<Result<Response>> obsolete(@Body ObsoleteReq req);

  @POST("/certificate/download")
  Call<Result<DownloadRes>> download(@Body DownloadReq req);

  @POST("/certificate/expirationDate")
  Call<Result<ExpirationRes>> expirationDate(@Body ExpirationReq req);

  @POST("/certificate/exists")
  Call<Result<ExistsRes>> exists(@Body ExistsReq req);

  @POST("/certificate/revoke")
  Call<Result<RevokeRes>> revoke(@Body RevokeReq req);

}
