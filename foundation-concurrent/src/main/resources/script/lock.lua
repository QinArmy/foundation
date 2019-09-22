--
-- Date: 2018/5/3
-- Time: 17:54
--

local value = redis.call('GET', KEYS[1])
if value == false or value == ARGV[1] then
    redis.call('SETEX', KEYS[1], ARGV[2], ARGV[1])
    value = ARGV[1]
end
return value
