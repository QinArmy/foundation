--
-- Date: 2018/5/3
-- Time: 17:54
--

local value = redis.call('GET', KEYS[1])
if value == false or value == ARGV[1] then
    redis.call('SET', KEYS[1], ARGV[1])
    redis.call('EXPIRE', KEYS[1], ARGV[2])
    value = ARGV[1]
end
return value