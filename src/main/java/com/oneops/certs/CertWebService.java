package com.oneops.certs;

import com.oneops.certs.model.CreateReq;
import com.oneops.certs.model.CreateRes;
import com.oneops.certs.model.CwsRequest;
import com.oneops.certs.model.CwsResponse;
import com.oneops.certs.model.DownloadReq;
import com.oneops.certs.model.DownloadRes;
import com.oneops.certs.model.ExistsRes;
import com.oneops.certs.model.ExpirationRes;
import com.oneops.certs.model.ExpiringReq;
import com.oneops.certs.model.ExpiringRes;
import com.oneops.certs.model.RenewReq;
import com.oneops.certs.model.RevokeReq;
import com.oneops.certs.model.RevokeRes;
import com.oneops.certs.model.SerialNumberRes;
import com.oneops.certs.model.ViewRes;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Certificate Web service interface.
 *
 * @author Suresh
 */
public interface CertWebService {

  @POST("/certificate/create")
  Call<CreateRes> create(@Body CreateReq createReq);

  @POST("/certificate/v2/renew")
  Call<CwsResponse> renew(@Body RenewReq req);

  @POST("/certificate/obsolete")
  Call<CwsResponse> obsolete(@Body CwsRequest req);

  @POST("/certificate/download")
  Call<DownloadRes> download(@Body DownloadReq req);

  @POST("/certificate/expirationDate")
  Call<ExpirationRes> expirationDate(@Body CwsRequest req);

  @POST("/certificate/serialNumber")
  Call<SerialNumberRes> serialNumber(@Body CwsRequest req);

  @POST("/certificate/expiring")
  Call<ExpiringRes> expiring(@Body ExpiringReq req);

  @POST("/certificate/exists")
  Call<ExistsRes> exists(@Body CwsRequest req);

  @POST("/certificate/view")
  Call<ViewRes> view(@Body CwsRequest req);

  @POST("/certificate/revoke")
  Call<RevokeRes> revoke(@Body RevokeReq req);
}
