import React from "react";
import axios from "axios";
import { render, screen, act } from "@testing-library/react";
import App from "./App";

jest.mock("axios");

describe("Basic testing of initial page", () => {
  
    it("Should show error message when service is down", async () => {
        const promise = Promise.resolve();

        // Mock
        axios.get.mockImplementation(() =>
            Promise.reject(new Error("Network Error"))
        );

        render(<App />);

        const errorText = await screen.findByText("Something is wrong with our server :(");
        expect(errorText).toBeDefined();

        await act(async () => await promise);
    });

    it("Should NOT show service list is when receives an EMPTY list from the server", async () => {
        const promise = Promise.resolve();

        // Mock
        const mockedServiceList = {data: { services: [] }};
        axios.get.mockResolvedValue(mockedServiceList);

        render(<App />);

        const serviceList = screen.findByTestId("service-list");
        expect(JSON.stringify(serviceList)).toEqual(JSON.stringify({}));

        await act(async () => await promise);
    });

    it("Should show service list is when receives NON-EMPTY list from the server", async () => {
        const promise = Promise.resolve();

        // Mock
        const mockedServiceList = {data: { services: [
            {
                Id: 1,
                Name: "Service UP",
                Url: "https://www.up.com",
                Valid: 1,
                Created: "2022-01-08T10:22:11",
                LastVerified: "2022-01-08T10:22:12"
              },
              {
                Id: 2,
                Name: "Service DOWN",
                Url: "https://www.down.com",
                Valid: 0,
                Created: "2022-01-08T10:22:11",
                LastVerified: "2022-01-08T10:21:12"
              },
        ] }};
        axios.get.mockResolvedValue(mockedServiceList);

        render(<App />);

        const service1UpBox = await screen.findAllByTestId("1-Green");
        const service1Name = await screen.findByText("Service UP");
        const service1Url = await screen.findByText("https://www.up.com");

        const service2DownBox = await screen.findAllByTestId("1-Green");
        const service2Name = await screen.findByText("Service DOWN");
        const service2Url = await screen.findByText("https://www.down.com");

        expect(service1UpBox).toBeDefined();
        expect(service1Name).toBeDefined();
        expect(service1Url).toBeDefined();

        expect(service2DownBox).toBeDefined();
        expect(service2Name).toBeDefined();
        expect(service2Url).toBeDefined();

        await act(async () => await promise);
    });

});
