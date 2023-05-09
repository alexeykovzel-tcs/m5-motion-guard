# this script allows for starting the build and bootRun phase of the server, and runs the pi after the server is online
echo "this script assumes that the pi is connected, and you have the peripherals set up"
echo "this means that the camera and motion sensor are connected"

# start the build phase and wait till its done
echo "Starting build phase"
./gradlew build & 
pid=$!
wait $pid
echo "Build phase done"

# run ./gradlew bootRun in tmux session
tmux new-session -d -s server './gradlew bootRun'
echo "starting server"

# save current time
start=$(date +%s)

# sleep 90
# echo "server should be online now"


echo "installing pi dependencies"
cd rpi/

#create python env if it doesnt exist
if [ ! -d ~/Documents/project/711NotWorking/cs22-35-main/rpi/.venv ]; then
    python3 -m venv .venv
    # activate environment and install dependencies
    source .venv/bin/activate
    pip install -r requirements.txt
else
    # activate environment
    source ~/Documents/project/711NotWorking/cs22-35-main/rpi/.venv/bin/activate
fi


# if not 90 seconds since start, wait until 90 seconds have passed
while [ $(($(date +%s)-start)) -lt 90 ]; do
    sleep 3
    echo "waiting for server to start - time left: $((90-$(($(date +%s)-start))))"
done

# run the pi
python pi.py
