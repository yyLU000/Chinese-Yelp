local key = KEYS[1];
local threadId = ARGV[1];
local releaseTime = ARGV[2];

if ( redis.call('hexists', key, threadId) == 0 ) then
    return nil;
end ;

local count = redis.call('hincrby', key, threadId, -1);

if ( count > 0 ) then
    redis.call('expire', key, releaseTime);
    return nil;
else
    redis.call('del', key);
    return nil;
end ;