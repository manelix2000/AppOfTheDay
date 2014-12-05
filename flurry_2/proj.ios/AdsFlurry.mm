#import "AdsFlurry.h"

#define OUTPUT_LOG(...)     if (self.debug) NSLog(__VA_ARGS__);

@implementation AdsFlurry

@synthesize debug = __debug;

template<typename T> string toString (const T &t)
{
    ostringstream oss;
    oss << t;
    return oss.str();
}

template<typename T> T fromString (const string &s)
{
    istringstream stream (s);
    T t;
    stream >> t;
    return t;
}

- (void) configDeveloperInfo: (NSMutableDictionary*) devInfo
{
    CCLog("PluginFlurry:  ****configDeveloperInfo is not available****");
}

- (void) showAds: (int) type size:(int) sizeEnum position:(int) pos
{
    if ([self showAdvert] == TRUE)
    {
        if ([FlurryAds adReadyForSpace:_adId])
        {
            CCLog("PluginFlurry:  ****Ad ready for space****");
            [FlurryAds displayAdForSpace:_adId onView:_viewController.view];
        }
        else
        {
            CCLog("PluginFlurry:  ****Ad is not ready for space****");
            [FlurryAds fetchAdForSpace:_adId frame:_viewController.view.frame size:FULLSCREEN];
        }
    }
}

- (void) hideAds: (int) type
{
    CCLog("PluginFlurry:  ****Hide ads****");
    [FlurryAds setAdDelegate:nil];
}

- (void) spendPoints: (int) points
{
    CCLog("PluginFlurry:  ****Flurry spendPoints are not available****");
}

- (void) setDebugMode: (BOOL) isDebugMode
{
    CCLog("PluginFlurry:  ****Flurry setDebugMode invoked(%d)****", isDebugMode);
    self.debug = isDebugMode;
    [Flurry setDebugLogEnabled:isDebugMode];
}

- (NSString*) getSDKVersion
{
    return @"5.3.0";
}

- (NSString*) getPluginVersion
{
    return @"1.0";
}

- (void) startSession: (NSString*) appKey
{
    [Flurry startSession:appKey];
    
    UIWindow *mainWindow = [UIApplication sharedApplication].keyWindow;
    _viewController = mainWindow.rootViewController;
    [FlurryAds initialize:_viewController];
    [FlurryAds setAdDelegate:self];
    
    _adId = @"AppOfTheDay";
}

- (void) setTime:(NSNumber *)timeInSeconds
{
    _originalTime = timeInSeconds;
    _time = [timeInSeconds floatValue];
    CCLog("PluginFlurry:  ****Time set:%f****", _time);
}

- (void) setAdId:(NSString *)adId
{
    _adId = adId;
    CCLog("PluginFlurry:  ****AdId set:%s****", [_adId UTF8String]);
}

- (NSString*) getAdId
{
    return _adId;
}

-(NSNumber*) getTime
{
    return _originalTime;
}

- (void) spaceDidReceiveAd:(NSString*)adSpace
{
    CCLog("PluginFlurry:  ****Space did received****");
    
    [FlurryAds displayAdForSpace:_adId onView:_viewController.view];
    
    ProtocolAds* pPlugin = dynamic_cast<ProtocolAds*>(PluginManager::getInstance()->loadPlugin("AdsFlurry"));
    pPlugin->onAdsResult(kAdsReceived, "space did received");
}

- (void) spaceDidFailToReceiveAd:(NSString*)adSpace error:(NSError *)error
{
    CCLog("PluginFlurry:  ****Space did fail tot receive ad****");
    
    ProtocolAds* pPlugin = dynamic_cast<ProtocolAds*>(PluginManager::getInstance()->loadPlugin("AdsFlurry"));
    pPlugin->onAdsResult(kUnknownError, "error");
}

- (void)spaceDidDismiss:(NSString *)adSpace interstitial:(BOOL)interstitial
{
    CCLog("PluginFlurry:  ****Space did dismiss****");
    
    ProtocolAds* pPlugin = dynamic_cast<ProtocolAds*>(PluginManager::getInstance()->loadPlugin("AdsFlurry"));
    pPlugin->onAdsResult(kFullScreenViewDismissed, "space did dismiss");
}

- (BOOL) spaceShouldDisplay:(NSString*)adSpace interstitial:(BOOL)interstitial
{
    CCLog("PluginFlurry:  ****Space should display****");
    return TRUE;
}

- (void) spaceDidRender:(NSString *)space interstitial:(BOOL)interstitial
{
    CCLog("PluginFlurry:  ****Space did render****");
}

- (void)saveTime
{
    time_t timer;
    time(&timer);
    string aux = toString(timer);
    
    CCUserDefault::sharedUserDefault()->setStringForKey("timer", aux);
    CCUserDefault::sharedUserDefault()->flush();
}

- (void) resetTime
{
    CCUserDefault::sharedUserDefault()->setStringForKey("timer", "");
    CCUserDefault::sharedUserDefault()->flush();
}

- (BOOL)showAdvert
{
    CCLog("PluginFlurry: inShowAdvert");
    if (_time > 0.0f)
    {
        string getTime = CCUserDefault::sharedUserDefault()->getStringForKey("timer");
        
        if (!strcmp(getTime.c_str(), ""))
        {
            [self saveTime];
            return TRUE;
        }
        else
        {
            time_t timer;
            time_t back = fromString<time_t>(getTime);
            
            time(&timer);
            float seconds = difftime(timer, back);
            
            CCLog("PluginFlurry:  ****Difference Seconds: %f****", seconds);
            
            if (seconds >= _time)
            {
                CCLog("PluginFlurry: ****Show Advert****");
                [self saveTime];
                return TRUE;
            }
        }
        return FALSE;
    }
    return TRUE;
}

@end