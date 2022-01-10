import React from "react";
import classes from './Spinner.module.css'
import { faSpinner } from "@fortawesome/free-solid-svg-icons"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

const Spinner = props => {
    return (
        <div className={classes.container}>
            <div className={classes.backdrop} onClick={props.onClose}></div>
            <FontAwesomeIcon className={`${classes.spinner} fa-3x`} icon={faSpinner}/>
        </div>
    );
};

export default Spinner;