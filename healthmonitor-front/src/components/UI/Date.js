import React from "react";
import Moment from "moment";

const formatDate = (date) => {
    return Moment(date).format('LLL');
}

const Date = (props) => {
    return <span>{formatDate(props.date)}</span>
};

export default Date;