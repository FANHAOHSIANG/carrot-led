package tw.carrot.led;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.*;
import java.util.Locale;
/** Optional visible trip meter. Coordinates stay in memory and are discarded on stop. */
final class TripLocation implements LocationListener {
  private final LocationManager manager;
  private TripMetrics metrics=new TripMetrics();
  private Location previous;
  private long accepted=-1;
  private boolean active;
  private String state="GPS 行程未啟用";
  TripLocation(Context context){manager=context.getSystemService(LocationManager.class);}
  static boolean permitted(Context c){return c.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED;}
  static boolean available(Context c){
    try{return permitted(c)&&c.getSystemService(LocationManager.class).isProviderEnabled(LocationManager.GPS_PROVIDER);}
    catch(Exception e){return false;}
  }
  void unavailable(){state="GPS 未啟用：請檢查精確位置權限與系統 GPS";}
  void start(){
    if(active)return;
    metrics=new TripMetrics();previous=null;accepted=-1;
    try {manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this,Looper.getMainLooper());active=true;state="GPS 等待定位";}
    catch(SecurityException|IllegalArgumentException e){state="GPS 無法啟用；請檢查精確位置權限與定位開關";}
  }
  public void onLocationChanged(Location l){
    long now=SystemClock.elapsedRealtime();
    if(!active||!l.hasAccuracy()||(previous!=null&&l.getElapsedRealtimeNanos()<=previous.getElapsedRealtimeNanos()))return;
    Double dt=previous==null?null:(l.getElapsedRealtimeNanos()-previous.getElapsedRealtimeNanos())/1e9;
    Double distance=previous==null?null:(double)previous.distanceTo(l);
    if(metrics.record(l.hasSpeed()?l.getSpeed():-1,l.getAccuracy(),(SystemClock.elapsedRealtimeNanos()-l.getElapsedRealtimeNanos())/1e9,distance,dt)){
      previous=new Location(l);accepted=now;state="GPS 行程";
    }
  }
  public void onProviderDisabled(String provider){state="GPS 定位已關閉";accepted=-1;previous=null;}
  public void onProviderEnabled(String provider){state="GPS 等待定位";}
  public void onStatusChanged(String provider,int status,Bundle extras){}
  String summary(){
    String speed=accepted>=0&&SystemClock.elapsedRealtime()-accepted<15000&&metrics.speedKmh!=null?String.format(Locale.TAIWAN,"%.1f km/h",metrics.speedKmh):"速度待定位";
    return active?state+" · "+speed+String.format(Locale.TAIWAN," · 估計里程 %.2f km",metrics.metres/1000):state;
  }
  void stop(){try{manager.removeUpdates(this);}catch(SecurityException ignored){}active=false;previous=null;accepted=-1;state="GPS 行程已停止";}
}
