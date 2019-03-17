--
-- Date: 2018/5/3
-- Time: 20:07
--

local value = redis.call('GET', KEYS[1])
if value == ARGV[1] then
    redis.call('DEL', KEYS[1])
    value = ARGV[1]
end
return value