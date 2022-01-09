import { useEffect, useRef } from "react";

//code from: https://usehooks-ts.com/react-hook/use-interval

export function useInterval (callback, delay) {
    const savedCallback = useRef();

    //Remember the latest callback
    useEffect(() =>{
        savedCallback.current = callback;
    }, [callback]);

    useEffect(() =>{
        function tick(){
            savedCallback.current();
        }

        if (delay) {
            const id = setInterval(tick, delay);
            return () => {
                clearInterval(id);
            }
        }
    }, [callback, delay]);
}