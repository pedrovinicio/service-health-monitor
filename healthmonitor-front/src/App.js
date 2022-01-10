import React, { useState, useEffect } from 'react';
import axios from "axios";
import AddService from './components/Services/AddService';
import ServicesList from './components/Services/ServicesList';
import MessageModal from './components/UI/MessageModal';
import ConfirmationModal from './components/UI/ConfirmationModal';
import EditServiceModal from './components/Services/EditServiceModal';
import * as Constants from "./constants/Constant"
import { useInterval } from './hooks/useInterval';
import AppHeader from './components/UI/AppHeader';
import ServerError from './components/UI/ServerError';
import Spinner from './components/UI/Spinner';

function App() {
  const [servicesList, setServicesList] = useState([]);
  const [message, setMessage] = useState()
  const [loading, setLoading] = useState({isLoading: false})
  const [confirmation, setConfirmation] = useState()
  const [edit, setEdit] = useState()
  const [serverError, setServerError] = useState(false)
  

  // Fetch all services
  useEffect(() => {
    fetchServices();
  }, []);

  // Set up polling
  useInterval(async () => {
    fetchServices();
  }, Constants.POLLING_INTERVAL);

  const fetchServices = async () => {
    try {
      const result = await axios.get(Constants.HEALTH_MONITOR_API);
      setServicesList(result.data.services);
      setServerError(false);
    } catch {
      setServerError(true);
    }
  };

  const addServiceHandler = async (sName, sUrl) => {
      try {
        setLoading({ isLoading: true });
        await axios.post(Constants.HEALTH_MONITOR_API, {
          name: sName,
          url: sUrl
        });
        setMessage({
          isError: false,
          title: "Add service",
          message: "New service " + sName + " added successfully."
        });
        setLoading({ isLoading: false });
        fetchServices();
      } catch {
        setMessage({
          isError: true,
          title: "Add service",
          message: "Something went wrong while adding new service " + sName + "."
        });
        setLoading({ isLoading: false });
      }
  };

  const openDeleteConfirmationModal = (service) => {
    setConfirmation({
      title: "Remove service confirmation",
      message: "Are you sure you want to remove " + service.Name + "?",
      service: service
    });
  };

  const removeServiceHandler = async (service) => {
    try {
      setConfirmation(null);
      await axios.delete(Constants.HEALTH_MONITOR_API, {
        data: { id: service.Id },
      });
      setMessage({
        isError: false,
        title: "Remove service",
        message: "Service " + service.Name + " removed successfully."
      });
      fetchServices();
    } catch {
      setMessage({
        isError: true,
        title: "Remove service",
        message: "Something went wrong while removing service " + service.Name + "."
      });
    }
  };

  const openUpdateServiceModal = (service) => {
    setEdit({
      service: service
    });
  };

  const updateServiceHandler = async (service, newName, newUrl) => {
      try {
        setEdit(null);
        await axios.put(Constants.HEALTH_MONITOR_API, {
          id: service.Id,
          name: newName,
          url: newUrl,
        });
        setMessage({
          isError: false,
          title: "Update service",
          message: "Service " + newName + " updated successfully."
        });
        fetchServices();
      } catch {
        setMessage({
          isError: true,
          title: "Update service",
          message: "Something went wrong while updating service " + service.Name + "."
        });
      }
  }

  const messageModalHandler = () => {
    setMessage(null);
  }

  const cancelDeleteConfirmationModal = () => {
    setConfirmation(null);
  }

  const cancelUpdateModal = () => {
    setEdit(null);
  }

  return (
    <div>
      <AppHeader>Service monitoring app</AppHeader>
      {loading.isLoading &&<Spinner></Spinner>}
      {message && <MessageModal title={message.title} message={message.message} isError={message.isError} onClose={messageModalHandler}></MessageModal>}
      {confirmation && <ConfirmationModal title={confirmation.title} message={confirmation.message} service={confirmation.service} onConfirm={removeServiceHandler} onCancel={cancelDeleteConfirmationModal}></ConfirmationModal>}
      {edit && <EditServiceModal service={edit.service} onSave={updateServiceHandler} onCancel={cancelUpdateModal}></EditServiceModal>}
      <AddService onAddService={addServiceHandler}></AddService>
      {serverError && <ServerError>Something is wrong with our server :(</ServerError>}
      {(!serverError && servicesList.length) && <ServicesList services={servicesList} onRemoveService={openDeleteConfirmationModal} onUpdateService={openUpdateServiceModal}></ServicesList>}
    </div>
  );
}

export default App;
