#import "InterfaceAds.h"
#import "FlurryAds.h"
#import "Flurry.h"

#import "ProtocolAds.h"
#import "PluginManager.h"

#include "cocos2d.h"
#include <string>
#include <sstream>
#include <iostream>

using namespace cocos2d;
using namespace cocos2d::plugin;
using namespace std;

@interface AdsFlurry : NSObject <InterfaceAds>
{
    UIViewController* _viewController;
}

@property               BOOL          debug;
@property               float         time;
@property (retain)      NSString *    adId;
@property (retain)      NSNumber *    originalTime;

//interfaces of protocol : InterfaceAds

- (void)                configDeveloperInfo: (NSMutableDictionary*) devInfo;
- (void)                showAds: (int) type size:(int) sizeEnum position:(int) pos;
- (void)                hideAds: (int) type;
- (void)                spendPoints: (int) points;
- (void)                setDebugMode: (BOOL) debug;
- (NSString*)           getSDKVersion;
- (NSString*)           getPluginVersion;

//interfaces of flurry SDK

- (void)                startSession: (NSString*) appKey;
- (void)                setAdId:(NSString *) adId;
- (void)                setTime: (NSNumber *)timeInSeconds;
- (NSString*)           getAdId;
- (NSNumber*)           getTime;
- (void)                resetTime;

//helper functions

- (void)                saveTime;
- (BOOL)                showAdvert;

@end
